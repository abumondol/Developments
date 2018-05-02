
# coding: utf-8

# In[1]:


import numpy as np


# In[3]:


def resample(data, rate, start_end_times = []):
    interval = 1/rate
    if len(start_end_times)==0:
        start_time, end_time = data[0, 0], data[-1, 0]        
    else:
        start_time, end_time = start_end_times
    
    ts = np.arange(start_time, end_time, interval)    
    count = len(ts)    
    res = np.zeros((count, data.shape[1]))
    res[:, 0] = ts
    
    print('Resampling... Rate: {}'.format(rate))
    print('Old start, end: {}, {}'.format(data[0,0], data[-1, 0]))
    print('New start, end: {}, {}'.format(start_time, end_time))
    print('Sample Count old, new: {}, {}'.format(len(data), len(ts)))
    
    j = 0
    while not(data[j, 0] <= ts[0] < data[j+1, 0]):
        j+=1    
    
    for i in range(count):
        while not(data[j, 0] <= ts[i] < data[j+1, 0]):
            j+=1        
        
        factor = (ts[i] - data[j, 0])/(data[j+1, 0] - data[j, 0]);
        res[i, 1:] = (1-factor)*data[j, 1:]  + factor*data[j+1, 1:]        
        
    print('Resampling Done')
    return res


# In[4]:


def time_to_index(data, ts):
    count = len(ts)
    ix = np.zeros(count)
    
    
    print('Time to index ... ')
    print('Data start, end: {}, {}'.format(data[0,0], data[-1, 0]))
    print('TS start, end: {}, {}'.format(ts[0], ts[-1]))    
    
    j = 0
    data_count = len(data)
    
    for i in range(count):            
        while j<data_count-1 and not(data[j, 0] <= ts[i] < data[j+1, 0]):
            j += 1
        
        if j==data_count-1:
            if ts[i] > data[j, 0]:                
                raise Exception('Time to index: time is greater than data limit')
            
            if i<count-1:
                raise Exception('Time to index: more than one beyond data limit')
                
        ix[i] = j
    
    ix = ix.astype(int)
    print('Time to index Done.')
    
    return ix        
    


# In[1]:


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
        q0 = 1-q1*q1 - q2*q2 - q3*q3
        q0[q0<0] = 0
        q0 = np.sqrt(q0)
    
    q0 = q0.reshape((nrows, 1))
    q1 = q1.reshape((nrows, 1))
    q2 = q2.reshape((nrows, 1))
    q3 = q3.reshape((nrows, 1))
        
    Rx = np.concatenate( ( q0*q0+q1*q1-q2*q2-q3*q3, 2*(q1*q2-q0*q3), 2*(q1*q3+q0*q2) ), axis=1)
    Ry = np.concatenate( ( 2*(q0*q3+q1*q2),  q0*q0-q1*q1+q2*q2-q3*q3, 2*(q2*q3 - q0*q1) ), axis=1)
    Rz = np.concatenate( ( 2*(q1*q3 - q0*q2), 2*(q2*q3+q0*q1), q0*q0-q1*q1-q2*q2+q3*q3 ), axis=1)
    
    return Rx, Ry, Rz

