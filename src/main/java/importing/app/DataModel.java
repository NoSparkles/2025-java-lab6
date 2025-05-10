package importing.app;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class DataModel {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty firstName, lastName, email, gender, country, domain, birthDate;

    public DataModel(int id, String firstName, String lastName, String email, String gender, String country, String domain, String birthDate) {
        this.id = new SimpleIntegerProperty(id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.email = new SimpleStringProperty(email);
        this.gender = new SimpleStringProperty(gender);
        this.country = new SimpleStringProperty(country);
        this.domain = new SimpleStringProperty(domain);
        this.birthDate = new SimpleStringProperty(birthDate);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return this.id; }

    public String getFirstName() { return firstName.get(); }
    public SimpleStringProperty firstNameProperty() { return this.firstName; }

    public String getLastName() { return lastName.get(); }
    public SimpleStringProperty lastNameProperty() { return this.lastName; }

    public String getEmail() { return email.get(); }
    public SimpleStringProperty emailProperty() { return this.email; }

    public String getGender() { return gender.get(); }
    public SimpleStringProperty genderProperty() { return this.gender; }

    public String getCountry() { return country.get(); }
    public SimpleStringProperty countryProperty() { return this.country; }

    public String getDomain() { return domain.get(); }
    public SimpleStringProperty domainProperty() { return this.domain; }

    public String getBirthDate() { return birthDate.get(); }
    public SimpleStringProperty birthDateProperty() { return this.birthDate; }
}