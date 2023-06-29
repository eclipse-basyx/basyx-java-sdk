package org.eclipse.basyx.submodel.types.handoverdocumentation.enums;

/**
 * DocumentClassification according to VDI 2770 Blatt 1: 2020
 */
public enum DocClass {
	Identification("01-01"),
	TechSpec("02-01"),
	Drawing("02-02"),
	Assembly("02-03"),
	Certificate("02-04"),
	Commissioning("03-01"),
	Operation("03-02"),
	Safety("03-03"),
	Maintenance("03-04"),
	Repair("03-05"),
	SpareParts("03-06"),
	Contract("04-01");

	public final String classID;

	DocClass(String classID) {
		this.classID = classID;
	}
}
