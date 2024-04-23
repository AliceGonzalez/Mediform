import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './form-status.reducer';

export const FormStatusDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const formStatusEntity = useAppSelector(state => state.formStatus.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="formStatusDetailsHeading">Form Status</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{formStatusEntity.id}</dd>
          <dt>
            <span id="formStatusID">Form Status ID</span>
          </dt>
          <dd>{formStatusEntity.formStatusID}</dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{formStatusEntity.status}</dd>
          <dt>Template Form</dt>
          <dd>{formStatusEntity.templateForm ? formStatusEntity.templateForm.id : ''}</dd>
          <dt>Child</dt>
          <dd>{formStatusEntity.child ? formStatusEntity.child.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/form-status" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/form-status/${formStatusEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default FormStatusDetail;
