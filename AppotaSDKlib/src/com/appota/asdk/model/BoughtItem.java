package com.appota.asdk.model;

public class BoughtItem {

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLimitNumber() {
		return limitNumber;
	}

	public void setLimitNumber(int limitNumber) {
		this.limitNumber = limitNumber;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getOwnedQuantity() {
		return ownedQuantity;
	}

	public void setOwnedQuantity(int ownedQuantity) {
		this.ownedQuantity = ownedQuantity;
	}

	public String getDescroption() {
		return descroption;
	}

	public void setDescription(String descroption) {
		this.descroption = descroption;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public int getTotalBoughtItem() {
		return totalBoughtItem;
	}

	public void setTotalBoughtItem(int totalBoughtItem) {
		this.totalBoughtItem = totalBoughtItem;
	}

	private int id;
	private int limitNumber;
	private String icon;
	private int level;
	private int price;
	private int ownedQuantity;
	private String descroption;
	private String name;
	private String target;
	private int totalBoughtItem;
}
