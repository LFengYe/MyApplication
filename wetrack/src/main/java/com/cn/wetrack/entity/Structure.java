package com.cn.wetrack.entity;

/**
 * 车队树结构
 */
public class Structure {
	/*车队*/
	private Motorcade motorcade;
	/*车辆*/
	private Vehicle[] vehicles;

	public Motorcade getMotorcade() {
		return motorcade;
	}

	public void setMotorcade(Motorcade motorcade) {
		this.motorcade = motorcade;
	}

	public Vehicle[] getVehicles() {
		return vehicles;
	}

	public void setVehicles(Vehicle[] vehicles) {
		this.vehicles = vehicles;
	}

}
