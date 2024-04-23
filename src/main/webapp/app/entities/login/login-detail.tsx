import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './login.reducer';

export const LoginDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const loginEntity = useAppSelector(state => state.login.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="loginDetailsHeading">Login</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{loginEntity.id}</dd>
          <dt>
            <span id="username">Username</span>
          </dt>
          <dd>{loginEntity.username}</dd>
          <dt>
            <span id="password">Password</span>
          </dt>
          <dd>{loginEntity.password}</dd>
          <dt>Parent ID</dt>
          <dd>{loginEntity.parentID ? loginEntity.parentID.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/login" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/login/${loginEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default LoginDetail;
