package species

import species.participation.Observation;
import species.utils.Utils;
import org.grails.rateable.*

class Resource implements Rateable {

	public enum ResourceType {
		ICON("Icon"),
		IMAGE("Image"),
		AUDIO("Audio"),
		VIDEO("Video");
		
		private String value;
		
		ResourceType(String value) {
			this.value = value;
		}
		
		public String value() {
			return this.value;
		}
	}
	
	ResourceType type;
	String url; //TODO validate as url
	String fileName;
	String description;
	String mimeType; //TODO:validate
    int rating = 0;

	def grailsApplication

	static hasMany = [contributors:Contributor, attributors:Contributor, speciesFields:SpeciesField, observation:Observation, licenses:License];
	static belongsTo = [SpeciesField, Observation];
	
	static mapping = {
		description type:'text';
		sort "id"
	}
	
    static constraints = {
		fileName(blank:false);
		url(nullable:true);
		description(nullable:true);
		mimeType(nullable:true);
		licenses  validator : { val, obj -> val && val.size() > 0 }
        rating(nullable:false, min:0, max:5);
    }
	
	String thumbnailUrl() {
		String thumbnailUrl = '';
		switch(type) {
			case  ResourceType.IMAGE :
				thumbnailUrl = this.fileName.trim().replaceFirst(/\.[a-zA-Z]{3,4}$/, grailsApplication.config.speciesPortal.resources.images.thumbnail.suffix);
				break;
			case ResourceType.VIDEO :				
				String videoId = Utils.getYouTubeVideoId(this.url);
				thumbnailUrl = "http://img.youtube.com/vi/${videoId}/default.jpg"
				break;
			default :
				log.error "Not a valid type"
		}		 
		return thumbnailUrl;
	}
	
	String getUrl() {
		if(this.type == ResourceType.IMAGE) {
            if(Utils.isAbsoluteURL(this.fileName)) {
    			return this.fileName;
            } else {
		        //def g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
                //return g.createLinkTo(base:grailsApplication.config.speciesPortal.observations.serverURL, this.fileName);
            }
		}
		return this.@url;
	}
	
	void setUrl(String url) {
		if(!url) return;
		if(Utils.isURL(url)) {
			this.url = url;
		}
	}
}
