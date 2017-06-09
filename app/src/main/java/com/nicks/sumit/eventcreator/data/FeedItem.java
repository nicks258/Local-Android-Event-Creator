package com.nicks.sumit.eventcreator.data;

public class FeedItem {
	private int id;
	private String eventName, eventDescription, image, profilePic, timeStamp, url, category, AssignTo;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	private String imagePath;
//	public FeedItem() {
//	}

//	public FeedItem(int id, String eventName, String image, String eventDescription,
//					String profilePic, String timeStamp, String url, String category, String AssignTo) {
//		super();
//		this.id = id;
//		this.eventName = eventName;
//		this.image = image;
//		this.eventDescription = eventDescription;
//		this.profilePic = profilePic;
//		this.timeStamp = timeStamp;
//		this.url = url;
//		this.AssignTo = AssignTo;
//		this.category = category;
//	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String getCategory(){
		return category;
	}
	public String getAssignTo(){
		return AssignTo;
	}
	public void setCategory(String category){
		this.category = category;
	}
	public void setAssignTo(String assignTo)
	{
		this.AssignTo = assignTo;
	}
	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getImge() {
		return image;
	}

	public void setImge(String image) {
		this.image = image;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
