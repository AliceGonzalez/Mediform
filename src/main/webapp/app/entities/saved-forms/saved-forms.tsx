import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, SORT } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './saved-forms.reducer';

export const SavedForms = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const savedFormsList = useAppSelector(state => state.savedForms.entities);
  const loading = useAppSelector(state => state.savedForms.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    } else {
      return order === ASC ? faSortUp : faSortDown;
    }
  };

  return (
    <div>
      <h2 id="saved-forms-heading" data-cy="SavedFormsHeading">
        Saved Forms
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/saved-forms/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Saved Forms
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {savedFormsList && savedFormsList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('savedFormID')}>
                  Saved Form ID <FontAwesomeIcon icon={getSortIconByFieldName('savedFormID')} />
                </th>
                <th className="hand" onClick={sort('formID')}>
                  Form ID <FontAwesomeIcon icon={getSortIconByFieldName('formID')} />
                </th>
                <th className="hand" onClick={sort('formType')}>
                  Form Type <FontAwesomeIcon icon={getSortIconByFieldName('formType')} />
                </th>
                <th>
                  Child <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Template Form <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {savedFormsList.map((savedForms, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/saved-forms/${savedForms.id}`} color="link" size="sm">
                      {savedForms.id}
                    </Button>
                  </td>
                  <td>{savedForms.savedFormID}</td>
                  <td>{savedForms.formID}</td>
                  <td>{savedForms.formType}</td>
                  <td>{savedForms.child ? <Link to={`/child/${savedForms.child.id}`}>{savedForms.child.id}</Link> : ''}</td>
                  <td>
                    {savedForms.templateForm ? (
                      <Link to={`/template-form/${savedForms.templateForm.id}`}>{savedForms.templateForm.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/saved-forms/${savedForms.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`/saved-forms/${savedForms.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/saved-forms/${savedForms.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Saved Forms found</div>
        )}
      </div>
    </div>
  );
};

export default SavedForms;
