package br.com.erudio.data.dto.V1;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import br.com.erudio.model.Book;

@Relation(collectionRelation = "people")
@JsonPropertyOrder({ "id", "firstName", "lastName", "address", "gender" })
public class PersonDTOV1 extends RepresentationModel<PersonDTOV1> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String firstName;

	private String lastName;

	@JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
	private Date birthDay;

	private String address;

	private String gender;

	private Boolean enabled;

	private String profileUrl;

	private String photoUrl;

	private List<Book> books;

	public PersonDTOV1() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@JsonIgnore
	public String getName() {
		return (firstName != null ? firstName : "") + (lastName != null ? " " + lastName : "");
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(address, birthDay, enabled, firstName, gender, id, lastName, photoUrl, profileUrl);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonDTOV1 other = (PersonDTOV1) obj;
		return Objects.equals(address, other.address) && Objects.equals(birthDay, other.birthDay)
				&& Objects.equals(enabled, other.enabled) && Objects.equals(firstName, other.firstName)
				&& Objects.equals(gender, other.gender) && Objects.equals(id, other.id)
				&& Objects.equals(lastName, other.lastName) && Objects.equals(photoUrl, other.photoUrl)
				&& Objects.equals(profileUrl, other.profileUrl);
	}

}
