public class Patient {
	private String name;
	private String phone_number;
	private String mac_address;
	
	public Patient(String name, String phone, String mac)
	{
		this.name = name;
		this.phone_number = phone;
		this.mac_address = mac;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getPhone()
	{
		return phone_number;
	}
	
	public String getMac()
	{
		return mac_address;
	}

	@Override
	public String toString() {
		return name + "-" + phone_number + "-" + mac_address;
	}
}