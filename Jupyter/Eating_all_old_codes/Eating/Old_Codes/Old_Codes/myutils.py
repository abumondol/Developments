
# coding: utf-8

# In[ ]:


import numpy as np
import data_manager as dm
import myparams


# In[ ]:


def smooth_data(data, factor):
    count = len(data)
    for i in range(1, count):
        data[i, 1:] = factor*data[i-1, 1:] + (1-factor)*data[i, 1:]    
    return data


# In[ ]:


def smooth_dataset(data, factor):
    print("Smothing data with factor ", factor, " ...")
    subject_count = len(data)
    res = []
    for subject in range(subject_count):        
        sess_count = len(data[subject])        
        for sess in range(sess_count):
            print("Subject ", subject, ", Session ", sess)
            data[subject][sess][0] = smooth_data(data[subject][sess][0], factor)
            
    return data


# In[ ]:


def normalize_data(x):    
    sq = np.multiply(x, x)
    mag = np.sqrt(np.sum(sq, axis = 2, keepdims=True))
    #print(mag.shape)    
    #mag = np.concatenate((mag, mag, mag), axis = 2)
    #print(mag.shape)
    x = np.divide(x, mag)
    return x

def normalize_dataset(data):
    #data = copy.deepcopy(data)
    for subject in range(len(data)):
        for sess in range(len(data[subject])):
            x = data[subject][sess]["x"]
            data[subject][sess]["x"] = normalize_data(x)
    return data


# In[ ]:


def get_segments_session(d, mp, left_len, right_len):
    mp_count = len(mp)    
    y = mp[:, -1]
    x = np.zeros((mp_count, left_len+right_len, 3))
    
    for i in range(mp_count):
        ix = mp[i, 0]
        x[i, :, :] = d[ix-left_len:ix+right_len, 1:]        
    
    res = {"x":x, "y":y}
    return res        

def get_segments_dataset(data, mp, left_len, right_len):    
    segments = []
    for subject in range(len(data)):        
        subject_segs = []                 
        for sess in range(len(data[subject])):
            print("Subject ", subject, ", Session ", sess)
            segs = get_segments_session(data[subject][sess][0], mp[subject][sess], left_len, right_len)
            subject_segs.append(segs)
            
        segments.append(subject_segs)
        
    return segments


# In[ ]:


def get_file_name_from_params(params):
    s = "len_"+ str(params["window_len_left"]) + "_" +str(params["window_len_left"])
    s = s + "_xth_"+str(int(-10*params["x_th_max"]))
    s = s + "_smooth_"+params["smooth_factor"]
    return s


# In[ ]:


#params = myparams.get_params()
#s = get_file_name_from_params(params)
#print(s)
#ds = dm.get_data("uva_lab_data", "data")
#ds= smooth_dataset(ds, params["smooth_factor"])

