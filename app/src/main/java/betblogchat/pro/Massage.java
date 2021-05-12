package betblogchat.pro;

class Massage {
    public String name;
    public String masage_server;
    public String data_time_server;
    public String data_time_client;
    public String massage_client;

    public Massage(String name, String masage_server, String data_time_server,String data_time_client, String massage_client)
    {
        this.name = name;
        this.masage_server = masage_server;
        this.data_time_server = data_time_server;
        this.data_time_client = data_time_client;
        this.massage_client = massage_client;
    }

    public String getData_time_client()
    {
        return data_time_client;
    }

    public void setData_time_client(String data_time_client)
    {
        this.data_time_client = data_time_client;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMasage_server()
    {
        return masage_server;
    }

    public void setMasage_server(String masage_server)
    {
        this.masage_server = masage_server;
    }

    public String getData_time_server()
    {
        return data_time_server;
    }

    public void setData_time_server(String data_time_server)
    {
        this.data_time_server = data_time_server;
    }

    public String getMassage_client()
    {
        return massage_client;
    }

    public void setMassage_client(String massage_client)
    {
        this.massage_client = massage_client;
    }
}
