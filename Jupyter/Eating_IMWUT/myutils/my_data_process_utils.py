
# coding: utf-8

# In[1]:


import numpy as np


# In[2]:


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


# In[3]:


def time_to_index(data, ts):
    count = len(ts)
    ix = np.zeros(ts.shape)
    
    
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
    


# In[4]:


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


# In[5]:


def normalize_by_magnitude(d):
    mag = np.sqrt(np.sum(d*d, axis=1)).reshape((-1, 1))
    d = d/mag
    return d


# In[6]:


def get_spatio_temporal_series(d, radius):
    radius2 = radius*radius    
    count = len(d)
    
    res = []
    c = d[0, :]
    si = 0
    for i in range(1, count):
        df = c - d[i, :]
        df = np.sum(df*df)
        if df>radius2:   
            res.append([si, i-1])
            c = d[i, :]
            si = i
            
    res.append([si, count-1])
    
    return np.array(res).astype(int)


# In[7]:


def smooth_data(data, factor):
    data = np.copy(data)
    count = len(data)
    for i in range(1, count):
        data[i, :] = factor*data[i-1, :] + (1-factor)*data[i, :]    
    return data


# In[8]:


def simple_moving_average(d, window_size):
    dcount = len(d)
    res = np.zeros((dcount, 1))
    h = window_size//2
    for i in range(dcount):
        si = max([0, i-h])
        ei = min([dcount, i+h])
        res[i]= np.mean(d[si:ei])
    return res
        
    
        


# In[9]:


def add_subj_sess_to_array(d, subj, sess, at_begin=True, to_int=False):    
    ss = np.zeros((len(d), 2))
    ss[:, 0] = subj
    ss[:, 1] = sess
    
    if at_begin:
        res = np.concatenate((ss, d), axis=1)
    else:
        res = np.concatenate((d, ss), axis=1)
        
    if to_int:
        res = res.astype(int)
        
    return res
    
    
    
    

