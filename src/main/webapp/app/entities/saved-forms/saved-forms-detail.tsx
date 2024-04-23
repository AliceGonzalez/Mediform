import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './saved-forms.reducer';

export const SavedFormsDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const savedFormsEntity = useAppSelector(state => state.savedForms.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="savedFormsDetailsHeading">Saved Forms</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{savedFormsEntity.id}</dd>
          <dt>
            <span id="savedFormID">Saved Form ID</span>
          </dt>
          <dd>{savedFormsEntity.savedFormID}</dd>
          <dt>
            <span id="formID">Form ID</span>
          </dt>
          <dd>{savedFormsEntity.formID}</dd>
          <dt>
            <span id="formType">Form Type</span>
          </dt>
          <dd>{savedFormsEntity.formType}</dd>
          <dt>Child</dt>
          <dd>{savedFormsEntity.child ? savedFormsEntity.child.id : ''}</dd>
          <dt>Template Form</dt>
          <dd>{savedFormsEntity.templateForm ? savedFormsEntity.templateForm.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/saved-forms" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/saved-forms/${savedFormsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default SavedFormsDetail;
