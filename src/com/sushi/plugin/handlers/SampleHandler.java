package com.sushi.plugin.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.internal.loader.EquinoxClassLoader;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.sushi.plugin.SushiSpecificParams;
import com.sushi.plugin.Test;
import com.sushi.plugin.preference.ui.IPreferenceConstants;
import com.sushi.plugin.preference.ui.SushiPreferencePage;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */

/*FUNZIONA PLUGIN MA ESECUZIONE NON VA A BUON FINE PERCHE' EVOSUITE NON FA NULLA.
 * HO PROVATO AD AUMENTARNE IL TIME BUDGET MA NON CAMBIA NULLA, POSSIBILE CRASHI PER VIA DELL'USO
 * DEL .jar SHADED? PROVA AD USARE NORMALE.
 */
public class SampleHandler extends AbstractHandler {

	static List<URL> urls = new ArrayList<URL>();

	Object firstElement;

	IJavaElement element = null;

	static String name;

	static String fullName;

	static String finalClasspath = "";

	@SuppressWarnings("restriction")
	CompilationUnit unit;

	IJavaProject project = null;

	IJavaElement ij = null;

	IJavaElement ij2 = null;

	IJavaElement ij3 = null;

	String[] cp = null;
	
	static URL locationURL = null;
	
	String locationString = null;
	
	static URL finalLocationURL = null;
	
	static URL finalURL = null;
	
	Class<?> testClass = null;
	
	Class<?> annotationClass = null;

	private static URLClassLoader loader;

	private SushiSpecificParams params;
	
	
	//PARAMETRI SPECIFICI CHE SOTTO VADO A PRENDERE DALL'ANNOTAZIONE
	private int generation_budget;
	private int synthesis_budget;
	private int global_budget = SushiPreferencePage.getGlobal_budget();
	private String tmp_base = null;
	private String out = null;
	private boolean use_mosa;
	//PARAMETRI GLOBALI
	private String CPsushi_master = SushiPreferencePage.getStore().getString(IPreferenceConstants.CPSUSHI_MASTER_PREFERENCE);
	private String jdk = SushiPreferencePage.getStore().getString(IPreferenceConstants.JDK_PREFERENCE);
	private String CPglpk_java = SushiPreferencePage.getStore().getString(IPreferenceConstants.CPGLPK_JAVA_PREFERENCE);
	private String glpkPath = SushiPreferencePage.getStore().getString(IPreferenceConstants.GLPKPATH_PREFERENCE);
	private String jbse_lib = SushiPreferencePage.getStore().getString(IPreferenceConstants.JBSE_LIB_PREFERENCE);
	private String sushi_lib = SushiPreferencePage.getStore().getString(IPreferenceConstants.SUSHI_LIB_PREFERENCE);
	private String z3 = SushiPreferencePage.getStore().getString(IPreferenceConstants.Z3_PREFERENCE)
			.substring(0, SushiPreferencePage.getStore().getString(IPreferenceConstants.Z3_PREFERENCE).length() - 4);
	private String evosuite = SushiPreferencePage.getStore().getString(IPreferenceConstants.EVOSUITE_PREFERENCE);

	@SuppressWarnings("restriction")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			firstElement = selection.getFirstElement();
			if (firstElement != null) {
				unit = (CompilationUnit) firstElement;
				name = unit.getElementName();
				IPath path = unit.getPath();
				IResource res = unit.getResource();
				URI location = res.getLocationURI();
				String modifiedFullName = null;
				
				try {
					
					 locationURL = location.toURL();
					 locationString = locationURL.toString().substring(0, 6) + "//" + locationURL.toString().substring(6, locationURL.toString().length());
					 finalLocationURL = new URL(locationString); //PERCHE' NON INSERISCE LE ALTRE DUE / ??
					 String partialLocation = locationString.substring(0, 46) + "bin/";
					 System.out.println(partialLocation);
					 finalURL = new URL(partialLocation); //NOTA IN FONDO
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				String pathToString = path.toString();
				fullName = pathToString.substring(23, pathToString.length() - 5);
				modifiedFullName = fullName.replace("/", ".");
				
				try {
					testClass = getClassFromFile(modifiedFullName);
					params = (SushiSpecificParams) testClass.getAnnotation(SushiSpecificParams.class);
					//estraggo i parametri specifici   
					//DEVO RIUSCIRE A RIFERIRMI AL VALORE FINALE DI PARAMS IN PREFERENCEPAGE
					generation_budget = params.generation_budget();
					synthesis_budget = params.synthesis_budget();
					global_budget = params.global_budget();
					tmp_base = params.tmp_base();
					out = params.out();
					use_mosa = params.use_mosa();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ij = unit.getParent();
				ij = (PackageFragment) ij;
				ij2 = ij.getParent();
				ij2 = (PackageFragmentRoot) ij2;
				ij3 = ij2.getParent();
				project = (IJavaProject) ij3;

				String separator = java.io.File.pathSeparator;

				try {
					cp = org.eclipse.jdt.launching.JavaRuntime.computeDefaultRuntimeClassPath(project);
				} catch (CoreException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				for (int i = 0; i < cp.length; i++) {
					finalClasspath = finalClasspath + cp[i] + separator;
				}

				//finalClasspath = finalClasspath.replace("\\", "\\\\");
				System.out.println(finalClasspath);

			} else
				return null;

		}	
		
		//CREAZIONE COMANDO 
		final List<String> command = new ArrayList<String>();
		
		command.add("\"" + jdk + "\\bin\\java.exe\"");
		command.add("-Xms16G");
		command.add("-Xmx16G");
		command.add("-classpath");
		command.add("\"" + CPsushi_master + ";" + jdk + "\\lib\\tools.jar" + ";" + CPglpk_java + "\"");
		command.add("-Djava.library.path=" + glpkPath);
		command.add("sushi.Main");
		command.add("-jbse_lib");
		command.add(jbse_lib);
		command.add("-sushi_lib");
		command.add(sushi_lib);
		command.add("-evosuite");
		command.add(evosuite);
		command.add("-use_mosa");
		command.add("-z3");
		command.add(z3);
		command.add("-classes");
		command.add(finalClasspath);
		command.add("-target_class");
		command.add(fullName);
		command.add("-tmp_base");
		command.add(tmp_base);
		command.add("-out");
		command.add(out);
		command.add("-generation_time_budget");
		command.add("" + generation_budget);
		command.add("-synthesis_time_budget");
		command.add("" + synthesis_budget);	
		command.add("-global_time_budget");
		command.add("" + global_budget);
		
		String commandLine = command.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", "");
		
		

		try {
			System.out.println(commandLine);
			runCommand(commandLine);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	
	
	    public static void runCommand(String cmd) throws Exception {
	        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd); //PLATFORM DEPENDENT, MA ALTRIMENTI NON SO COME FARE
	        builder.redirectErrorStream(true);
	        Process p = builder.start();
	        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line;
	        while (true) {
	            line = r.readLine();
	            if (line == null) { break; }
	            System.out.println(line);
	        }
	    }
	    
	    public static Class<?> getClassFromFile(String fullClassName) throws Exception {
	    	if(loader == null) {
	        	loader = new URLClassLoader(new URL[] {finalURL}, Thread.currentThread().getContextClassLoader());
	        	
	        }
	        return loader.loadClass(fullClassName);
	        /*ORA TROVA AVLTREE.CLASS PERCHE' TORNA INDIETRO E VA IN /BIN, 
	        *FAI MODIFICHE PER FAR SI' CHE LE TROVI SEMPRE
	        */
	    }
	    
	   
	
}

