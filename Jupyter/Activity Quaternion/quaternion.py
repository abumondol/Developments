
# coding: utf-8

# In[1]:

import numpy as np


# In[18]:

def quat2mat(q):
    nrows, ncols = q.shape
    if ncols == 4:
        q0 = q[:, 0]
        q1 = q[:, 1]
        q2 = q[:, 2]
        q3 = q[:, 3]
    else:
        q1 = q[:, 0]
        q2 = q[:, 1]
        q3 = q[:, 2]        
        q0 = np.sqrt(1-q1*q1 - q2*q2 - q3*q3)
    
    q0 = q0.reshape((nrows, 1))
    q1 = q1.reshape((nrows, 1))
    q2 = q2.reshape((nrows, 1))
    q3 = q3.reshape((nrows, 1))
        
    Rx = np.concatenate( ( q0*q0+q1*q1-q2*q2-q3*q3, 2*(q1*q2-q0*q3), 2*(q1*q3+q0*q2) ), axis=1)
    Ry = np.concatenate( ( 2*(q0*q3+q1*q2),  q0*q0-q1*q1+q2*q2-q3*q3, 2*(q2*q3 - q0*q1) ), axis=1)
    Rz = np.concatenate( ( 2*(q1*q3 - q0*q2), 2*(q2*q3+q0*q1), q0*q0-q1*q1-q2*q2+q3*q3 ), axis=1)
    
    return Rx, Ry, Rz


# g = np.array([-0.16589468717575073,-1.7540171146392822,9.647087097167969])
# q = np.array([[-0.04325199872255325,-0.0791499987244606,0.9150339961051941], [-0.043783001601696014,-0.07859399914741516,0.9145169854164124]])
# print(q.shape)
# Rx, Ry, Rz = quat2mat(q)
# print(Rz)
# mg = np.sqrt(np.sum(g*g))
# g1 = g/mg
# print(g1)
# print(np.sum(g1*g1))
