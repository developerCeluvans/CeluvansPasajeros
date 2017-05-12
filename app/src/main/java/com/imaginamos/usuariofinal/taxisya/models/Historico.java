package com.imaginamos.usuariofinal.taxisya.models;

public class Historico {

	private String idService;
	private String idDriver;
	private String fecha;
	private String foto;
	private String nombre;
	private String placa;
	private String marca;
	private String modelo;
	private String telefono;
	
	public Historico(String idService, String idDriver, String fecha,
			String foto, String nombre, String placa, String marca,
			String modelo, String telefono) {
		super();
		this.idService = idService;
		this.idDriver = idDriver;
		this.fecha = fecha;
		this.foto = foto;
		this.nombre = nombre;
		this.placa = placa;
		this.marca = marca;
		this.modelo = modelo;
		this.telefono = telefono;
	}
	public String getIdService() {
		return idService;
	}
	public void setIdService(String idService) {
		this.idService = idService;
	}
	public String getIdDriver() {
		return idDriver;
	}
	public void setIdDriver(String idDriver) {
		this.idDriver = idDriver;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getFoto() {
		return foto;
	}
	public void setFoto(String foto) {
		this.foto = foto;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getPlaca() {
		return placa;
	}
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
	
}
