package com.imaginamos.usuariofinal.taxisya.models;

public class Agendado {
	private String id;
	private String driverId;
	private String dateTime;
	private String type;
	private String index;
	private String comp1;
	private String comp2;
	private String no;
	private String barrio;
	private String observaciones;
	private String estado;
	private String driverFoto;
	private String driverName;
	private String driverLastname;
	private String carBrand;
	private String carModel;
	private String carPlaca;
	private String fullAddress;
	private String destinationAddress;
	
	public Agendado(String id, String driverId, String dateTime, String type,
			String index, String comp1, String comp2, String no, String barrio,
			String observaciones, String estado, String fullAddress, String destinationAddress) {
		super();
		this.id = id;
		this.driverId = driverId;
		this.dateTime = dateTime;
		this.type = type;
		this.index = index;
		this.comp1 = comp1;
		this.comp2 = comp2;
		this.no = no;
		this.barrio = barrio;
		this.observaciones = observaciones;
		this.estado = estado;
		this.fullAddress = fullAddress;
		this.destinationAddress = destinationAddress;
		System.out.println("Estado Objeto: "+estado);
	}
	
	public Agendado(String id, String driverId, String dateTime, String type,
			String index, String comp1, String comp2, String no, String barrio,
			String observaciones, String estado, String driverFoto, String driverName, String driverLastname, String carBrand, String carModel, String carPlaca, String fullAddress, String destinationAddress) {
		super();
		this.id = id;
		this.driverId = driverId;
		this.dateTime = dateTime;
		this.type = type;
		this.index = index;
		this.comp1 = comp1;
		this.comp2 = comp2;
		this.no = no;
		this.barrio = barrio;
		this.observaciones = observaciones;
		this.estado = estado;
		this.driverFoto = driverFoto;
		this.driverName = driverName;
		this.driverLastname = driverLastname;
		this.carBrand = carBrand;
		this.carModel = carModel;
		this.carPlaca = carPlaca;
		this.fullAddress = fullAddress;
		this.destinationAddress = destinationAddress;
		System.out.println("Estado Objeto: "+estado);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getComp1() {
		return comp1;
	}
	public void setComp1(String comp1) {
		this.comp1 = comp1;
	}
	public String getComp2() {
		return comp2;
	}
	public void setComp2(String comp2) {
		this.comp2 = comp2;
	}
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getBarrio() {
		return barrio;
	}
	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getDriverFoto() {
		return driverFoto;
	}

	public void setDriverFoto(String driverFoto) {
		this.driverFoto = driverFoto;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverLastname() {
		return driverLastname;
	}

	public void setDriverLastname(String driverLastname) {
		this.driverLastname = driverLastname;
	}

	public String getCarBrand() {
		return carBrand;
	}

	public void setCarBrand(String carBrand) {
		this.carBrand = carBrand;
	}

	public String getCarModel() {
		return carModel;
	}

	public void setCarModel(String carModel) {
		this.carModel = carModel;
	}

	public String getCarPlaca() {
		return carPlaca;
	}

	public void setCarPlaca(String carPlaca) {
		this.carPlaca = carPlaca;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public String getDestinationAddress() {
		return destinationAddress;
	}

	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

}
