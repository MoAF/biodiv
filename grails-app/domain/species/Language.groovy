package species

class Language {
	public static final String DEFAULT_LANGUAGE = "English";
	private static final Random NUMBER_GENERATOR = new Random();
	
	String threeLetterCode;
	String twoLetterCode;
	String name;
	String region;
	//to identify language for curation
	boolean isDirty = false;
	
    static constraints = {
		threeLetterCode(blank:false, nullable:false, unique:true);
		twoLetterCode(nullable:true);
		name(blank:false, nullable:false);
		region(blank:true, nullable:true);
		isDirty(blank:false, nullable:false);
    }
	
	static mapping = {
		version false;
		sort 'name';
	}
	
	
	public static Language getLanguage(String languageName){
		Language lang = null;
		
		if(!languageName || languageName.trim() == ""){
			lang = Language.findByNameIlike(DEFAULT_LANGUAGE);
		}else{ 
			lang = Language.findByNameIlike(languageName.trim());
			if(!lang){
				//inserting new language
				lang = Language.findByNameIlike(DEFAULT_LANGUAGE);
				/*lang = new Language(name:languageName.trim(), threeLetterCode:getThreeLetterCode(languageName), isDirty:true);
				if(!lang.save(flush:true)){
					//println "Error during new language save $languageName"
					lang = null;
				}*/
			}	
		}
		return lang;
    }
	
	private static String getThreeLetterCode(String languageName){
		//TODO fix this
		int i = 0;
		while(++i < 100){
			//getting a 3 digit number
			String number = "" + (Math.abs((NUMBER_GENERATOR.nextInt() + 100) % 1000)) ;
			if(!Language.findByThreeLetterCode(number)){
				return number;
			}
		}
		//println "Invalid ThreeLetterCode. please give unique code"
		return null;
	}
	
	public static filteredList(){
		return Language.findAllByIsDirtyOrRegionIsNotNull(true).collect{it.name;} ;
	}

	//List of all languages to be used for common name
	public static allLanguage(){
		return Language.getAll().collect{it.name;} ;
	}
}
