package in.projectjwt.main.dtos;


public class ProfileUpdateDto {
	 private Integer id;
	    private String fullName;
	    private String address;
	    private String phone;
//	    private String photo;
	    
	    public Integer getId() {
	        return id;
	    }

	    public void setId(Integer id) {
	        this.id = id;
	    }

	    public String getFullName() {
	        return fullName;
	    }

	    public void setFullName(String fullName) {
	        this.fullName = fullName;
	    }

	    public String getAddress() {
	        return address;
	    }

	    public void setAddress(String address) {
	        this.address = address;
	    }

	    public String getPhone() {
	        return phone;
	    }

	    public void setPhone(String phone) {
	        this.phone = phone;
	    }

//	    public String getPhoto() {
//	        return photo;
//	    }
//
//	    public void setPhoto(String photo) {
//	        this.photo = photo;
//	    }
	    
	    

}
