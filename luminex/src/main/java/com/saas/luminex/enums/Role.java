package com.saas.luminex.enums;

/**
 * Defines the four access roles in the LumiNex platform.
 *
 * SUPER_ADMIN – Full platform control: manage admins, global settings, billing plans, audit logs.
 * ADMIN       – Operational management: clients, employees, requests, payments, content.
 * EMPLOYEE    – Task execution: view assigned requests, update progress, knowledge base.
 * CLIENT      – Self-service portal: submit requests, track status, manage payments.
 */
public enum Role {
    SUPER_ADMIN,
    ADMIN,
    EMPLOYEE,
    CLIENT
}
