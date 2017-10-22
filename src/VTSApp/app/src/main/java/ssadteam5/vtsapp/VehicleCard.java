package ssadteam5.vtsapp;


import org.json.JSONObject;

public class VehicleCard
{
    private String name;
    private String id;
    private String account;
    private String description;
    private JSONObject vehicleDetailsDO;
    private JSONObject driverDetailsDO;

    public JSONObject getVehicleDetailsDO()
    {
        return vehicleDetailsDO;
    }

    public JSONObject getDriverDetailsDO()
    {
        return driverDetailsDO;
    }



    public String getAccount()
    {
        return account;
    }

    public void setAccount(String account)
    {
        this.account = account;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public VehicleCard(String name, String account, String description,JSONObject vehicleDetailsDO,JSONObject driverDetailsDO)
    {
        this.name = name;
        this.account = account;
        this.description = description;
        this.vehicleDetailsDO = vehicleDetailsDO;
        this.driverDetailsDO = driverDetailsDO;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public VehicleCard(String name)
    {
        this.name = name;
    }
}
