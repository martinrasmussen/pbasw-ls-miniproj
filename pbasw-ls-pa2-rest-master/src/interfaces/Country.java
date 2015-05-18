package interfaces;

public interface Country {

    String getName();

    void setName(String name);

    String getCapital();

    void setCapital(String capitalCity);

    String getISO();

    void setISO(String iso2Code);

    double getLongitude();

    void setLongitude(double longitude);

    double getLatitude();

    void setLatitude(double latitude);

    String getEditableText();
    void setEditableText(String text);

    String getCurrency();
    void setCurrency(String currency);

}
