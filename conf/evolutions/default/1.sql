# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table contact (
  id                        bigint auto_increment not null,
  first_name                varchar(255) not null,
  last_name                 varchar(255) not null,
  company_name              varchar(255),
  email                     varchar(255) not null,
  phone_number              varchar(255) not null,
  user_id                   bigint,
  constraint pk_contact primary key (id))
;

create table image (
  id                        bigint auto_increment not null,
  content                   longblob,
  content_type              varchar(255),
  constraint pk_image primary key (id))
;

create table role (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  constraint pk_role primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  fb_id                     varchar(255),
  username                  varchar(255) not null,
  password                  varchar(255),
  email                     varchar(255),
  role_id                   bigint,
  type                      integer not null,
  image_id                  bigint,
  constraint ck_user_type check (type in (0,1)),
  constraint pk_user primary key (id))
;

alter table contact add constraint fk_contact_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_contact_user_1 on contact (user_id);
alter table user add constraint fk_user_role_2 foreign key (role_id) references role (id) on delete restrict on update restrict;
create index ix_user_role_2 on user (role_id);
alter table user add constraint fk_user_image_3 foreign key (image_id) references image (id) on delete restrict on update restrict;
create index ix_user_image_3 on user (image_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table contact;

drop table image;

drop table role;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

