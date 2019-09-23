package com.sushi.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SushiSpecificParams {
	
	public String tmp_base() default "C:\\Users\\tolf9\\git\\sushi-experiments2\\sushi-out"; //dovrebbero essere path ma in un'annotazione non posso usare Path
	public String out() default "C:\\Users\\tolf9\\git\\sushi-experiments2\\sushi-test";
	public boolean use_mosa() default true;
	public int global_budget() default 240;
	public int generation_budget() default 30;
	public int synthesis_budget() default 30;
	
	//I PARAMETRI SPECIFICI DEVONO S O S T I T U I R E QUELLI GLOBALI!!
	
	
	/*POI CI SONO ANCHE Classpath, path di GLPK, path di JBSE_lib, path di Evosuite e path di Z3 CHE PERO' SARANNO
	 * SOLO NELLA PAGINA DELLE PREFERENCES PERCHE' VENGONO SETTATI UNA VOLTA PER QUALSIASI PROGETTO/CLASSE
	 */
	
}
