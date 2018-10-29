///*********************************************************************************
// * Licensed Materials - Property of IBM
// * 5655-TAC
// * (C) Copyright IBM Corp. 2014 All Rights Reserved.
// * US Government Users Restricted Rights - Use, duplication or
// * disclosure restricted by GSA ADP Schedule Contract with
// * IBM Corp.               
// *********************************************************************************/
//
//package com.ibm.ims.ea.om.cmd.utilities;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Field;
//import java.util.Collection;
//import java.util.List;
//
//import javax.xml.bind.annotation.XmlElement;
//
//
//public class ParserPrototype {
//
//	public final static String PARAOP = "(";
//	public final static String PARAC = ")";
//	public final static String SPACE =" ";
//	public final static String COMMA =",";
//	
//	public String commandType;
//	
//	/**
//	 * Parses subcommands (UpdateTran, Query Tran etc.)
//	 * @param subCommand
//	 * @param clazz
//	 * @return
//	 */
//	public String parse(Object subCommand, Class<?> clazz) {
//		StringBuilder cmd = new StringBuilder();
//		try {
//			Class<?> commandClass = clazz;
//			//Get all fields
//			Field[] fields = commandClass.getDeclaredFields();
//			for (Field field: fields) {
//				//Check if annotation is of type XmlElement
//				if (field.isAnnotationPresent(XmlElement.class)) {
//					//If so, retrieve annotation
//					Annotation[] annotations = field.getDeclaredAnnotations();
//					//Set fields to be accessible
//					field.setAccessible(true);
//					//loop through annotaitons (only one annotation in this cas)
//					for (Annotation annotateField: annotations) {
//						XmlElement anno = (XmlElement) annotateField;
//						//get keyword
//						String prefix = anno.name();
//						if (!Collection.class.isAssignableFrom(field.getType())) {
//							Object object = field.get(subCommand);
//							if (object != null) {
//								retrieveCommand(prefix, object, cmd);
//							}
//						} else {
//							List<?> list = (List<?>) field.get(subCommand);
//							//if list exists, get attributes
//							if (list != null) {
//								retrieveCommand(prefix, list, cmd);
//							}
//						}
//					}
//				}
//
//			}
//			format(cmd);
//			return cmd.toString();
//
//		}
//
//		catch (SecurityException e) {
//
//		} catch (IllegalArgumentException e) {
//		} catch (IllegalAccessException e) {
//		}
//
//		return null;
//	}
//
//
//	public void retrieveCommand(String prefix, Object object, StringBuilder cmd) {
//		if (prefix.equalsIgnoreCase("SET") && commandType.equals("DB")) {
//			cmd.append(retrieveSet((com.ibm.ims.ea.om.cmd.v12.update.db.SET) object));
//			return;
//		}
//		else if (prefix.equalsIgnoreCase("SET") || commandType.equals("TRAN"))	 {
//			cmd.append(retrieveSet((com.ibm.ims.ea.om.cmd.v12.update.tran.SET) object));
//			return;
//		}
//
//		if (object != null) {
//			cmd.append(prefix).append(PARAOP).append(object).append(PARAC).append(SPACE);
//		}
//
//	}
//
//	/**
//	 * Loops through the list, getting all attributes and appends them
//	 * to command
//	 * @param prefix
//	 * @param list
//	 * @param cmd
//	 */
//	public void retrieveCommand(String prefix, List<?> list, StringBuilder cmd) {
//		
//		if (list.size() > 0) {
//			cmd.append(prefix).append(PARAOP);
//			for (int i = 0; i <list.size() ; i++) {
//				cmd.append(list.get(i));
//				if (i != list.size()-1) {
//					cmd.append(SPACE);
//				}
//			}
//			cmd.append(PARAC).append(SPACE);
//		}
//
//	}
//
//
//	/**
//	 * Loops through SET objects retrieving all attributes. A SET can be thought of as a 
//	 * sub-subcommand, it in itself contains lists of attributes. 
//	 * @param set
//	 * @return
//	 */
//	public String retrieveSet(com.ibm.ims.ea.om.cmd.v12.update.db.SET set) {
//		StringBuilder cmd = new StringBuilder();
//		cmd.append("SET").append(PARAOP);
//		try {
//			Class<?> SET = com.ibm.ims.ea.om.cmd.v12.update.db.SET.class;
//			Field[] fields = SET.getDeclaredFields();
//			for (Field field: fields) {
//				if (field.isAnnotationPresent(XmlElement.class)) {
//					Annotation[] annotations = field.getDeclaredAnnotations();
//					field.setAccessible(true);
//					for (Annotation annotateField: annotations) {
//						XmlElement anno = (XmlElement) annotateField;
//						List<?> obj = (List<?>) field.get(set);
//						if (obj != null && obj.size() > 0) {
//							cmd.append(anno.name()).append(PARAOP);
//							for (int i = 0; i < obj.size(); i++) {
//								if (obj.get(i) != null) {
//									cmd.append(obj.get(i));
//								}
//								if (i != obj.size()-1) {
//									cmd.append(SPACE);
//								}
//							}
//							cmd.append(PARAC).append(SPACE);
//						}
//					}
//				}
//			}
//		}
//		catch (SecurityException e) {
//
//		} catch (IllegalArgumentException e) {
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		format(cmd);
//		cmd.append(PARAC).append(PARAC);
//		return cmd.toString();
//	}
//	
//	public String retrieveSet(com.ibm.ims.ea.om.cmd.v12.update.tran.SET set) {
//		StringBuilder cmd = new StringBuilder();
//		cmd.append("SET").append(PARAOP);
//		try {
//			Class<?> SET = com.ibm.ims.ea.om.cmd.v12.update.tran.SET.class;
//			Field[] fields = SET.getDeclaredFields();
//			for (Field field: fields) {
//				if (field.isAnnotationPresent(XmlElement.class)) {
//					Annotation[] annotations = field.getDeclaredAnnotations();
//					field.setAccessible(true);
//					for (Annotation annotateField: annotations) {
//						XmlElement anno = (XmlElement) annotateField;
//						List<?> obj = (List<?>) field.get(set);
//						if (obj != null && obj.size() > 0) {
//							cmd.append(anno.name()).append(PARAOP);
//							for (int i = 0; i < obj.size(); i++) {
//								if (obj.get(i) != null) {
//									cmd.append(obj.get(i));
//								}
//								if (i != obj.size()-1) {
//									cmd.append(SPACE);
//								}
//							}
//							cmd.append(PARAC).append(SPACE);
//						}
//					}
//				}
//			}
//		}
//		catch (SecurityException e) {
//
//		} catch (IllegalArgumentException e) {
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		format(cmd);
//		cmd.append(PARAC).append(PARAC);
//		return cmd.toString();
//	}
//
//	public void format(StringBuilder cmd) {
//		cmd.deleteCharAt(cmd.length()-1);
//
//	}
//
//}
