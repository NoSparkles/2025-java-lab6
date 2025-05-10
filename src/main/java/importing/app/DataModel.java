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
    public SimpleIntegerProperty idProperty() { return id; }

    public String getFirstName() { return firstName.get(); }
    public SimpleStringProperty firstNameProperty() { return firstName; }

    public String getLastName() { return lastName.get(); }
    public SimpleStringProperty lastNameProperty() { return lastName; }

    public String getEmail() { return email.get(); }
    public SimpleStringProperty emailProperty() { return email; }

    public String getGender() { return gender.get(); }
    public SimpleStringProperty genderProperty() { return gender; }

    public String getCountry() { return country.get(); }
    public SimpleStringProperty countryProperty() { return country; }

    public String getDomain() { return domain.get(); }
    public SimpleStringProperty domainProperty() { return domain; }

    public String getBirthDate() { return birthDate.get(); }
    public SimpleStringProperty birthDateProperty() { return birthDate; }
}