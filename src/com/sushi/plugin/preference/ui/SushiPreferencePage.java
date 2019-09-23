package com.sushi.plugin.preference.ui;

import org.eclipse.jface.preference.PathEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.sushi.plugin.Activator;
import com.sushi.plugin.SushiSpecificParams;
import com.sushi.plugin.handlers.SampleHandler;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;

public class SushiPreferencePage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {
	
	 private static IPreferenceStore store = null;
	
	public SushiPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		
		store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		
	}

	//PARAMETRI GLOBLALI
	private static String CPsushi_master;
	private static String jdk;
	private static String CPglpk_java;
	private static String glpkPath;
	private static String jbselibPath;    //dovrebbero essere path ma in un'annotazione non posso usare Path
	private static String sushilibPath;
	private static String evosuitePath;
	private static String z3Path;
	//PARAMETRI SPECIFICI MODIFICABILI VIA ANNOTAZIONE
	private static String tmp_base;  
	private static String out;
	private static boolean use_mosa;
	private static int global_budget;
	private static int generation_budget;
	private static int synthesis_budget;
	private static String selected_class;
	
	
	SushiSpecificParams parameters = null;
	Class<?> testClass = null;
	private static URLClassLoader loader;
	private static URL finalURL = null;
	private static String class_name = null;
	
	
	// G & S
	
	public static String getCPsushi_master() {
		return CPsushi_master;
	}

	public static void setCPsushi_master(String cPsushi_master) {
		CPsushi_master = cPsushi_master;
	}

	public static String getJdk() {
		return jdk;
	}

	public static void setJdk(String jdkpar) {
		jdk = jdkpar;
	}

	public static String getCPglpk_java() {
		return CPglpk_java;
	}

	public static void setCPglpk_java(String cPglpk_java) {
		CPglpk_java = cPglpk_java;
	}
	
	public static IPreferenceStore getStore() {
		return store;
	}
	
	public static String getTmp_base() {
		return tmp_base;
	}

	public static void setTmp_base(String tmp_base) {
		SushiPreferencePage.tmp_base = tmp_base;
	}

	public static String getOut() {
		return out;
	}

	public static void setOut(String out) {
		SushiPreferencePage.out = out;
	}

	public static boolean isUse_mosa() {
		return use_mosa;
	}

	public static void setUse_mosa(boolean use_mosa) {
		SushiPreferencePage.use_mosa = use_mosa;
	}

	public static int getGlobal_budget() {
		return global_budget;
	}

	public static void setGlobal_budget(int global_budget) {
		SushiPreferencePage.global_budget = global_budget;
	}

	public static int getGeneration_budget() {
		return generation_budget;
	}

	public static void setGeneration_budget(int generation_budget) {
		SushiPreferencePage.generation_budget = generation_budget;
	}

	public static int getSynthesis_budget() {
		return synthesis_budget;
	}

	public static void setSynthesis_budget(int synthesis_budget) {
		SushiPreferencePage.synthesis_budget = synthesis_budget;
	}


	public static String getGlpkPath() {
		return glpkPath;
	}

	public static void setGlpkPath(String glpkPath) {
		SushiPreferencePage.glpkPath = glpkPath;
	}

	public static String getJbselibPath() {
		return jbselibPath;
	}

	public static void setJbselibPath(String jbselibPath) {
		SushiPreferencePage.jbselibPath = jbselibPath;
	}

	public static String getSushilibPath() {
		return sushilibPath;
	}

	public static void setSushilibPath(String sushilibPath) {
		SushiPreferencePage.sushilibPath = sushilibPath;
	}

	public static String getEvosuitePath() {
		return evosuitePath;
	}

	public static void setEvosuitePath(String evosuitePath) {
		SushiPreferencePage.evosuitePath = evosuitePath;
	}


	public static String getZ3Path() {
		return z3Path;
	}

	public static void setZ3Path(String z3Path) {
		SushiPreferencePage.z3Path = z3Path;
	}

	
	FileFieldEditor CPsushi_masterField;
	DirectoryFieldEditor jdkField;
	FileFieldEditor CPglpk_javaField;
	DirectoryFieldEditor glpkPathField;
	FileFieldEditor jbselibPathField;
	FileFieldEditor sushilibPathField;
	FileFieldEditor evosuitePathField;
	FileFieldEditor z3PathField;
	DirectoryFieldEditor tmpBasePathField; //
	DirectoryFieldEditor outPathField;
	BooleanFieldEditor useMosaField;
	IntegerFieldEditor globalBudgetField;
	IntegerFieldEditor generationBudgetField;
	IntegerFieldEditor synthesisBudgetField;
	FileFieldEditor selectedClassField;
	
	
	protected void createFieldEditors() {
		CPsushi_masterField = new FileFieldEditor(IPreferenceConstants.CPSUSHI_MASTER_PREFERENCE, "Sushi_master .jar File:", getFieldEditorParent());
		addField(CPsushi_masterField);
		
		jdkField = new DirectoryFieldEditor(IPreferenceConstants.JDK_PREFERENCE, "Java JDK directory:", getFieldEditorParent());
		addField(jdkField);
		
		CPglpk_javaField = new FileFieldEditor(IPreferenceConstants.CPGLPK_JAVA_PREFERENCE, "GLPK_JAVA .jar File:", getFieldEditorParent());
		addField(CPglpk_javaField);
		
		glpkPathField = new DirectoryFieldEditor(IPreferenceConstants.GLPKPATH_PREFERENCE, "GLPK Binaries:", getFieldEditorParent());
		addField(glpkPathField);
		
		jbselibPathField = new FileFieldEditor(IPreferenceConstants.JBSE_LIB_PREFERENCE, "JBSE-LIB .jar File:", getFieldEditorParent());
		addField(jbselibPathField);
		
		sushilibPathField = new FileFieldEditor(IPreferenceConstants.SUSHI_LIB_PREFERENCE, "SUSHI-LIB .jar File:", getFieldEditorParent());
		addField(sushilibPathField);
		
		evosuitePathField = new FileFieldEditor(IPreferenceConstants.EVOSUITE_PREFERENCE, "Evosuite .jar File:", getFieldEditorParent());
		addField(evosuitePathField);
				
		z3PathField = new FileFieldEditor(IPreferenceConstants.Z3_PREFERENCE, "Z3 Binaries Directory:", getFieldEditorParent());
		addField(z3PathField);
		
		tmpBasePathField = new DirectoryFieldEditor(IPreferenceConstants.TMP_BASE_PREFERENCE, "Tmp_Base Directory:", getFieldEditorParent());
		addField(tmpBasePathField);
		
		outPathField = new DirectoryFieldEditor(IPreferenceConstants.OUT_PREFERENCE, "Out Directory:", getFieldEditorParent());
		addField(outPathField);
		
		useMosaField = new BooleanFieldEditor(IPreferenceConstants.USE_MOSA_PREFERENCE, " Use MOSA", getFieldEditorParent());
		addField(useMosaField);
		
		globalBudgetField = new IntegerFieldEditor(IPreferenceConstants.GLOBAL_BUDGET_PREFERENCE, "Global Budget (seconds):", getFieldEditorParent());
		addField(globalBudgetField);
		
		generationBudgetField = new IntegerFieldEditor(IPreferenceConstants.GENERATION_BUDGET_PREFERENCE, "Generation Budget (seconds):", getFieldEditorParent());
		addField(generationBudgetField);
		
		synthesisBudgetField = new IntegerFieldEditor(IPreferenceConstants.SYNTHESIS_BUDGET_PREFERENCE, "Synthesis Budget (seconds):", getFieldEditorParent());
		addField(synthesisBudgetField);
		
		selectedClassField = new FileFieldEditor(IPreferenceConstants.SELECTED_CLASS_PREFERENCE, "Import @SushiSpecificParams from class:", getFieldEditorParent());
		addField(selectedClassField);
		
		//CI SIAMO CON I VALORI DI DEFAULT
		
	}
	
	

	@Override
	public void init(IWorkbench workbench) {
		
		CPsushi_master = store.getString(IPreferenceConstants.CPSUSHI_MASTER_PREFERENCE);
		jdk = store.getString(IPreferenceConstants.JDK_PREFERENCE);
		CPglpk_java = store.getString(IPreferenceConstants.CPGLPK_JAVA_PREFERENCE);
		glpkPath = store.getString(IPreferenceConstants.GLPKPATH_PREFERENCE);
		jbselibPath = store.getString(IPreferenceConstants.JBSE_LIB_PREFERENCE);
		sushilibPath = store.getString(IPreferenceConstants.SUSHI_LIB_PREFERENCE);
		evosuitePath = store.getString(IPreferenceConstants.EVOSUITE_PREFERENCE);
		z3Path = store.getString(IPreferenceConstants.Z3_PREFERENCE);
		selected_class = store.getString(IPreferenceConstants.SELECTED_CLASS_PREFERENCE);
		
		
		
		
		String x = store.getString(IPreferenceConstants.EVOSUITE_PREFERENCE);
		System.out.println(x);
		
	}
	
	/* if(SampleHandler.getParams())  VOGLIO FARE UN CONTROLLO SUI VALORI DELLE ANNOTAZIONI PER I PARAMETRI SPECIFICI
	 * MA COME LI RECUPERO DA PARAMS IN SAMPLEHANDLER? SE VADO A PRENDERE SOLO PARAMS MI PRENDE IL VALORE INIIZALE NULL
	 * GIUSTAMENTE, MI SERVE IL VALORE FINALE. DEVO FARE TUTTO IN UN METODO CON UN RETURN FINALE?
	 * 
	 * VADO A MODIFICARE IL PERFORMAPPLY(): OVVERO, INSERISCO UN NUOVO CAMPO "IMPORTA PARAMETRI SPECIFICI DA CLASSE:" IN
	 * CUI FACCIO SELEZIONARE UN PERCORSO AD UN FILE, CON CUI OTTENGO ISTANZA FILE DA CUI FACCIO UN GETCLASSFROMFILE,
	 * SELEZIONO LA CLASSE E LA USO PER CONTROLLARE SE SONO PRESENTI ANNOTAZIONI SUSHISPECIFICPARAMETERS. IN CASO, 
	 * QUANDO PREMO "APPLICA" SOSTITUISCO I PARAMETRI CON QUELLI DELL'ANNOTAZIONE.
	 */
	
	@Override
	protected void performApply() {
		
		performOk();
		String pathToClass = store.getString(IPreferenceConstants.SELECTED_CLASS_PREFERENCE);
		if(store.getString(pathToClass) != null) {
			try {
				testClass = getClassFromFile(pathToClass);
				parameters = (SushiSpecificParams) testClass.getAnnotation(SushiSpecificParams.class);

				store.setValue(IPreferenceConstants.USE_MOSA_PREFERENCE, parameters.use_mosa());

				store.setValue(IPreferenceConstants.GENERATION_BUDGET_PREFERENCE, parameters.generation_budget());

				store.setValue(IPreferenceConstants.GLOBAL_BUDGET_PREFERENCE, parameters.global_budget());

				store.setValue(IPreferenceConstants.OUT_PREFERENCE, parameters.out());

				store.setValue(IPreferenceConstants.SYNTHESIS_BUDGET_PREFERENCE, parameters.synthesis_budget());

				store.setValue(IPreferenceConstants.TMP_BASE_PREFERENCE, parameters.tmp_base());
				
				System.out.println(store.getInt(IPreferenceConstants.GLOBAL_BUDGET_PREFERENCE));
				
				initialize();  //UPDATE DEI VALORI GRAFICI DEI FIELD EDITORS
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//globalBudgetField.load();
		
		/* LA SOSTITUZIONE DEL VALORE CON QUELLO DELLE ANNOTAZIONI HA EFFETTO PERCHE' SE STAMPO IL VALORE E' QUELLO DELLA
		 * ANNOTAZIONE MODIFICATA, NON IL VALORE DI DEFAULT, PERO' NON RIESCO A VISUALIZZARLO UNA VOLTA PREMUTO APPLICA 
		 * NELLA PAGINA DI PREFERENZE
		 */
			
	}
	
	public static URL getFinalURL(String stringURL) {
		String modifiedClassname = "file:/" + stringURL.substring(0, 38).replace("\\", "/") + "bin/";
		System.out.println(modifiedClassname);
		try {
			return new URL(modifiedClassname);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Class<?> getClassFromFile(String pathToClass) throws Exception {
		finalURL = getFinalURL(pathToClass);
		if(loader == null) {
        	loader = new URLClassLoader(new URL[] {finalURL}, Thread.currentThread().getContextClassLoader());
      
        }
		
		class_name = pathToClass.substring(42, 50) + pathToClass.substring(50, pathToClass.length() - 5).replace("\\", ".");

        return loader.loadClass(class_name);
        /*ORA TROVA AVLTREE.CLASS PERCHE' TORNA INDIETRO E VA IN /BIN, 
        *FAI MODIFICHE PER FAR SI' CHE LE TROVI SEMPRE
        */
    }
	
	//file:/C:/Users/tolf9/git/sushi-experiments2/bin/
	
	/* ORA COME ORA FUNZIONA SOLO SE PRIMA DI LANCIARE IL COMANDO APRO LA PAGINA DELLE PREFERENZE, 
	 * PERCHE' SOLO COSI' SI AVVIA IL METODO INIT() E VENGONO SETTATI I VALORI. NON RIESCO ALTRIMENTI
	 * PERCHE' SE PROVO A FARE UN METODO SETVALUES() E SETDEFAULT() HO PROBLEMI CON LO "STATIC"
	 * CHE DEVE ESSERE NEL METODO CHIAMATO MA SETPREFERENCESTORE() NON LO E' QUINDI NON POSSO
	 */
	
	
	/*Fare in modo che comunichino con i valori reali di SUSHI, vedere se ci sono altri valori globali,
	 * fare valori locali con annotazione da classe java sotto test, vedere roba del salvataggio dei valori e retrieving
	 */

}
