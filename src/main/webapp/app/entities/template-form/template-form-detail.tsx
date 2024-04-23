import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './template-form.reducer';

export const TemplateFormDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const templateFormEntity = useAppSelector(state => state.templateForm.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="templateFormDetailsHeading">Template Form</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{templateFormEntity.id}</dd>
          <dt>
            <span id="templateFormID">Template Form ID</span>
          </dt>
          <dd>{templateFormEntity.templateFormID}</dd>
          <dt>
            <span id="formType">Form Type</span>
          </dt>
          <dd>{templateFormEntity.formType}</dd>
        </dl>
        <Button tag={Link} to="/template-form" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/template-form/${templateFormEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TemplateFormDetail;
