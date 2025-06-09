package org.jono.medicalmodelsservice.model;

import java.util.List;

public record LoginCompanies(String name, List<LoginUser> users) {
}
