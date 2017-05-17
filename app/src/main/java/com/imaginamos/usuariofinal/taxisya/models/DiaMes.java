package com.imaginamos.usuariofinal.taxisya.models;

public class DiaMes {

	private String dia;
	private String mes;
	private String anio;
	private String dayWeek;
	
	public DiaMes(String dia, String mes, String anio, String dayWeek) {
		super();
		this.dia = dia;
		this.mes = mes;
		this.anio = anio;
		this.dayWeek = dayWeek;
	}
	public String getDia() {
		return dia;
	}
	public void setDia(String dia) {
		this.dia = dia;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
	}
	public String getAnio() {
		return anio;
	}
	public void setAnio(String anio) {
		this.anio = anio;
	}
	public String getDayWeek() {
		return dayWeek;
	}
	public void setDayWeek(String dayWeek) {
		this.dayWeek = dayWeek;
	}
}
