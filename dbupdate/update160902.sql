

--liquibase formatted sql

--changeset delgado:160902-1
CREATE TABLE usuario_papel
(  
  papel VARCHAR(50) NOT NULL,
  usuario_id bigint not null,
  CONSTRAINT usuario_id_fk FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);
--rollback drop table usuario_papel
CREATE UNIQUE INDEX usuario_papel_uindex ON usuario_papel (usuario_id, papel);
--rollback DROP INDEX public.usuario_papel_uindex CASCADE


--changeset coutinho:160902-2
alter table proposicao ADD estado varchar(20) default 'FORADEPAUTA';
--rollback alter table proposicao drop estado

--changeset coutinho:160902-3
alter table proposicao ADD parecer_sal varchar(5000);
--rollback alter table proposicao drop parecer_sal

--changeset coutinho:160902-5
alter table proposicao ADD explicacao_sal varchar(5000);
--rollback alter table proposicao drop explicacao_sal

--changeset coutinho:160902-6
CREATE TABLE documento
(
  id BIGINT PRIMARY KEY NOT NULL,
  nome VARCHAR(128) NOT NULL,
  path VARCHAR(512),  
  updated timestamp not null,
  created timestamp not null,
  usuario_id bigint,
  CONSTRAINT doc_owner_id_fk FOREIGN KEY (usuario_id) REFERENCES usuario (id)	
);
--rollback drop table documento
--changeset coutinho:160902-7
CREATE TABLE proposicao_notatecnica (
  id                BIGINT PRIMARY KEY NOT NULL,
  datacriacao       TIMESTAMP NOT NULL,  
  proposicao_id     BIGINT NOT NULL,
  usuario_id        BIGINT NOT NULL,
  nota 				VARCHAR(512),
  documento_id        BIGINT ,
  url_arquivo		VARCHAR(256),
  FOREIGN KEY (proposicao_id) REFERENCES proposicao (id),
  FOREIGN KEY (documento_id) REFERENCES documento (id),
  FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);
--rollback drop table proposicao_notatecnica
