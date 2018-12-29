import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Board from './board';
import BoardDetail from './board-detail';
import BoardUpdate from './board-update';
import BoardDeleteDialog from './board-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={BoardUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={BoardUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={BoardDetail} />
      <ErrorBoundaryRoute path={match.url} component={Board} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={BoardDeleteDialog} />
  </>
);

export default Routes;
