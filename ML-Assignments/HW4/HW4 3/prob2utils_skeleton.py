# Solution routine for matrix factorization
# CSCI 6140 HW4


import numpy as np

def grad_U(Ui, Yij, Vj, reg, eta):
    """
    Takes as input Ui (the ith row of U), a training point Yij, the column
    vector Vj (jth column of V^T), reg (the regularization parameter lambda),
    and eta (the learning rate).

    Returns the gradient of the regularized loss function with
    respect to Ui multiplied by eta.
    """
    
    reg_term = reg * Ui
    error_term = (Yij - (np.dot(Ui, Vj))) * Vj
    gradient = reg_term - error_term 
    return (eta * gradient)


def grad_V(Vj, Yij, Ui, reg, eta):
    """
    Takes as input the column vector Vj (jth column of V^T), a training point Yij,
    Ui (the ith row of U), reg (the regularization parameter lambda),
    and eta (the learning rate).

    Returns the gradient of the regularized loss function with
    respect to Vj multiplied by eta.
    """
    reg_term = reg * Vj
    error_term = (Yij - (np.dot(Ui, Vj))) * Ui
    gradient = reg_term - error_term 
    return (eta * gradient)

def get_err(U, V, Y, reg=0.0):
    """
    Takes as input a matrix Y of triples (i, j, Y_ij) where i is the index of a user,
    j is the index of a movie, and Y_ij is user i's rating of movie j and
    user/movie matrices U and V.

    Returns the mean regularized squared-error of predictions made by
    estimating Y_{ij} as the dot product of the ith row of U and the jth column of V^T.
    """
    sqrd_error = 0
    for i in range(len(Y)):
        (i, j, Y_ij) = Y[i]
        sqrd_error += 0.5 * (Y_ij - (np.dot(U[i - 1], V[j - 1])))**2
    val = sqrd_error / (len(Y))
    return val


def train_model(M, N, K, eta, reg, Y, eps=0.0001, max_epochs=300):
   
    """
    Given a training data matrix Y containing rows (i, j, Y_ij)
    where Y_ij is user i's rating on movie j, learns an
    M x K matrix U and N x K matrix V such that rating Y_ij is approximated
    by (UV^T)_ij.

    Uses a learning rate of <eta> and regularization of <reg>. Stops after
    <max_epochs> epochs, or once the magnitude of the decrease in regularized
    MSE between epochs is smaller than a fraction <eps> of the decrease in
    MSE after the first epoch.

    Returns a tuple (U, V, err) consisting of U, V, and the unregularized MSE
    of the model.
    """
    # U matrix initialized to random numbers
    U = np.random.uniform(-0.5, 0.5, (M, K))
    # V matrix initialized to random numbers
    V = np.random.uniform(-0.5, 0.5, (N, K))
    
    # MSE of the first epoch
    curr_MSE_err = get_err(U, V, Y)
    
    # list of indices
    indices = list(range(len(Y)))
    
    # on shuffling indices
    shuffled_indices = np.random.permutation(indices)
       
    # computing new U and V using gradient descent    
    for i in range(len(Y)):
        i = shuffled_indices[i]
        (i, j, y_val) = Y[i]
        U[i - 1] = U[i - 1] - grad_U(U[i - 1], y_val, V[j - 1], reg, eta)
        V[j - 1] = V[j - 1] - grad_V(V[j - 1], y_val, U[i - 1], reg, eta)
        
        
    # calculating MSE for new U and Y matrix generated    
    new_MSE_err = get_err(U, V, Y)
    difference = (curr_MSE_err - new_MSE_err)
    curr_MSE_err = new_MSE_err
    epoch_cnt = 1
    decrease = difference
    while (epoch_cnt < max_epochs and decrease > eps * difference):
        
         # on shuffling indices
        indices = list(range(len(Y)))
        shuffled_indices = np.random.permutation(indices)
        
        # computing new U and V using gradient descent    
        for i in range(len(Y)):
            i = shuffled_indices[i]
            (i, j, Y_ij) = Y[i]
            U[i - 1] = U[i - 1] - grad_U(U[i - 1], Y_ij, V[j - 1], reg, eta)
            V[j - 1] = V[j - 1] - grad_V(V[j - 1], Y_ij, U[i - 1], reg, eta)
        
        #calculating new MSE
        new_MSE_err = get_err(U, V, Y)
        decrease = curr_MSE_err - new_MSE_err
        curr_MSE_err = new_MSE_err
        epoch_cnt += 1
        
    return (U, V, curr_MSE_err)


