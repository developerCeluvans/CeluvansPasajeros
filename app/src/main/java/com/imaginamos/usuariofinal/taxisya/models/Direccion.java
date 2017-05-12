package com.imaginamos.usuariofinal.taxisya.models;

import com.google.gson.annotations.SerializedName;

public class Direccion {

	@SerializedName("id")
	private String id;
	@SerializedName("index_id")
	private String index;
	@SerializedName("comp1")
	private String comp1;
	@SerializedName("comp2")
	private String comp2;
	@SerializedName("no")
	private String numero;
	@SerializedName("barrio")
	private String barrio;
	@SerializedName("obs")
	private String observaciones;
	@SerializedName("name")
	private String name;
	@SerializedName("address")
	private String address;
	@SerializedName("lat")
	private String lat;
	@SerializedName("lng")
	private String lng;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public Direccion(String id, String index, String comp1, String comp2, String numero,
			String barrio, String observaciones, String name) {
		super();
		this.id = id;
		this.index = index;
		this.comp1 = comp1;
		this.comp2 = comp2;
		this.numero = numero;
		this.barrio = barrio;
		this.observaciones = observaciones;
		this.name = name;
	}


	public String getId() {return id;}
	public void setId(String id) {this.id = id;}
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
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
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
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
}
