package org.acme.config.entities;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "configurations")
public class ConfigurationsEntity {
    @Id
    String name;
    String value;
    Date updated_at;
}
