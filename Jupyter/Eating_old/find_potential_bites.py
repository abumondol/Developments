
# coding: utf-8

# In[ ]:

import numpy as np
import pickle
import sys
import myparams
import myutils
import data_manager as dm


# In[ ]:

def find_min_points_by_xth(accel, params):
    x_th_max = params["x_th_max"]
    min_bite_interval = params["min_bite_interval"]
    window_len_left = params["window_len_left"]
    window_len_right = params["window_len_right"]
    window_len = window_len_left + window_len_right    
    step_length = min_bite_interval//2
    
    x = accel[:, 1]
    count = len(x)
    
    mp = []
    for i in range(0, count-step_length, step_length):
        min_index = i
        for j in range(i, i+step_length):
            if x[j] < x[min_index]:
                min_index = j
                
        if x[min_index] <= x_th_max and min_index > window_len and min_index < count-window_len:
            mp.append(min_index)
    
    return mp


# In[ ]:

def filter_min_points_by_neighbor(mp, accel, params):
    if len(mp)<=1:
        return mp
    
    min_bite_interval = params["min_bite_interval"]
    window_len_left = params["window_len_left"]
    window_len_right = params["window_len_right"]
    window_len = window_len_left + window_len_right    
    step_length = min_bite_interval//2
    
    x = accel[:, 1]
    while True:
        res = []
        count = len(mp)
        ix = mp[0]
        ixRight = mp[1]
        if ixRight - ix > min_bite_interval or x[ix] < x[ixRight]:
            res.append(ix)
        
        for i in range(1, count - 1):
            ix = mp[i]
            ixLeft = mp[i - 1]
            ixRight = mp[i + 1]

            cond_left = ix - ixLeft > min_bite_interval or x[ix] <= x[ixLeft]
            cond_right = ixRight - ix > min_bite_interval or x[ix] < x[ixRight]        

            if cond_left and cond_right:
                res.append(ix)
        
        ix = mp[count - 1]
        ixLeft = mp[count - 2]
        if ix - ixLeft > min_bite_interval or x[ix] <= x[ixLeft]:
            res.append(ix)            
        
        if len(mp) == len(res):
            break        
        mp = res
        #print("     mp count: ", len(mp))
    
    return res


# In[4]:

def filter_min_points_by_feature(mp, accel, params):
    var_th = params["var_th"]
    window_len_left = params["window_len_left"]
    window_len_right = params["window_len_right"]
    window_len = window_len_left + window_len_right
    
    res = [];
    count = len(mp)
    for i in range(count):
        ix = mp[i]
        v = np.sum(np.var(accel[ix-window_len_left:ix+window_len_right+1, 1:], axis = 0))
        if v >= var_th:
            res.append(ix)
    return res    


# In[ ]:

def label_min_points_lab(mp, annots, no_filter, params):
    window_len_left = params["window_len_left"]
    window_len_right = params["window_len_right"]
    window_len = window_len_left + window_len_right
    max_annot_distance = params["max_annot_distance"]
    exclude_annot_distance = params["exclude_annot_distance"]
    
    mp_count = len(mp)
    if mp_count == 0:
        return mp
    
    res = np.zeros((mp_count, 3))      
    annot_count = len(annots)
    
    for i in range(mp_count):
        res[i, 0] = mp[i]
        j = np.argmin( np.abs(annots[:, 0] - mp[i]) )
        if np.abs(annots[j, 0] - mp[i]) <= max_annot_distance:
            res[i, 1] = annots[j, 1]
            res[i, 2] = 1
        elif np.abs(annots[j, 0] - mp[i]) <= exclude_annot_distance:
             res[i, 1] = -1
    
    if no_filter:
        res = res.astype(int)
        return res
    
    res = res[res[:, 1]>=0, :]
    while True:
        flag = True
        mp_count = len(res)        
        for i in range(1, mp_count-1):
                if res[i, 1] == 0 and ((res[i-1, 1]>0 and res[i, 0] - res[i-1, 0]<=window_len) or (res[i+1, 1]>0 and res[i+1, 0] - res[i, 0]<=window_len)):
                    res[i, 1] = -1
                    flag = False
        
        res = res[res[:, 1]>=0, :]        
        if flag:
            if res[0, 1] == 0 and res[1, 1]>0 and res[1, 0] - res[0, 0]<=window_len:
                res[0, 1] = -1
                
            mp_count = len(res)
            if res[mp_count-1, 1] == 0 and res[mp_count-2, 1]>0 and res[mp_count-1, 0] - res[mp_count-2, 0]<=window_len:
                res[mp_count-1, 1] = -1
                
            res = res[res[:, 1]>=0, :]            
            break    
    res = res.astype(int)
    return res
    


# In[ ]:

def label_min_points_free(mp, annots):
    mp_count = len(mp)
    if mp_count == 0:
        return mp
    
    res = np.zeros((mp_count, 3))    
    annot_count = len(annots)    
    
    for i in range(mp_count):
        res[i, 0] = mp[i]            
        if  annot_count > 0:
            for j in range(annot_count):
                if annots[j, 0] <= mp[i] <= annots[j, 1]:
                    res[i, 1] = annots[j, 2]
                    res[i, 2] = 1
                    break

    res = res.astype(int)
    return res


# In[ ]:

def get_summary_lab(mp, annots, print_flag = False):
    res = np.zeros((2, 5))
    res[0, 0] = sum(mp[:, 1]==0)
    res[0, 1] = sum(mp[:, 1]==1)
    res[0, 2] = sum(mp[:, 1]==2)
    res[0, 3] = res[0, 1] + res[0, 2]
    res[0, 4] = len(mp)
    
    res[1, 1] = sum(annots[:, 1]==1)
    res[1, 2] = sum(annots[:, 1]==2)
    res[1, 3] = len(annots)
            
    if print_flag:
        print_summary_lab(res)
    return res

def print_summary_lab(res):
    print("Neg MP count: ", res[0, 0])
    print("Bite MP count (detected/gt): ", res[0, 1], "/", res[1, 1] )
    print("Drink MP count (detected/gt): ", res[0, 2], "/", res[1, 2] )
    print("Pos MP count (detected/gt): ", res[0, 3], "/", res[1, 3]  )        
    print("Total MP count: ", res[0, 4])
    print("")
    


# In[ ]:

def get_summary_free(mp, print_flag = False):
    res = np.zeros((1, 5))
    res[0, 0] = sum(mp[:, 1]==0)
    res[0, 1] = sum(mp[:, 1]==1)
    res[0, 2] = sum(mp[:, 1]==2)
    res[0, 3] = sum(mp[:, 1]==3)
    res[0, 4] = len(mp)
    
    if print_flag:
        print_summary_free(res)
    return res

def print_summary_free(res):
    print("Neg MP count: ", res[0, 0])
    print("Meal MP count: ", res[0, 1])
    print("Snack MP count: ", res[0, 2])                
    print("Drink MP count: ", res[0, 3])
    print("Total MP count: ", res[0, 4])        
    print("")


# In[ ]:

def find_min_points_for_dataset(ds, no_filter_lab, params, print_summary=False):
    subject_count = len(ds)
    ds_mp = []
    res = []
    for subject in range(subject_count):
        subject_data = ds[subject]
        sess_count = len(subject_data)
        subject_mp =[]
        for sess in range(sess_count):
            if print_summary:
                print("\nSubject: {}, Sess: {}".format(subject, sess))
            sess_data = subject_data[sess]
            accel = sess_data[0]
            annots = sess_data[1]
            sess_mp = find_min_points_by_xth(accel, params)
            sess_mp = filter_min_points_by_neighbor(sess_mp, accel, params)
            sess_mp = filter_min_points_by_feature(sess_mp, accel, params)
            if len(annots)==0 or annots.shape[1] == 3:
                sess_mp = label_min_points_free(sess_mp, annots)
                if len(res)==0:
                    res = get_summary_free(sess_mp, print_summary)
                else:
                    res = res + get_summary_free(sess_mp, print_summary)
            else:                
                sess_mp = label_min_points_lab(sess_mp, annots, no_filter_lab, params)                
                if len(res)==0:
                    res = get_summary_lab(sess_mp, annots, print_summary)
                else:
                    res = res + get_summary_lab(sess_mp, annots, print_summary)
                    
            subject_mp.append(sess_mp)
        ds_mp.append(subject_mp)
        
    return ds_mp, res    


# In[ ]:

def find_min_points_segments_all(params, print_summary = False):    
    mps = [0, 0, 0, 0, 0, 0]
    segs = [0, 0, 0, 0, 0, 0]
    ds_names = ["uva_lab", "steven_lab", "uva_free", "steven_free"]
    
    for i in range(4):
        ds = dm.get_data(ds_names[i]+"_data_"+params["smooth_factor"], subdir = "data")        
        
        print("*********", ds_names[i], " Summary **********")        
        mps[i], res = find_min_points_for_dataset(ds, False, params, print_summary)
        segs[i] = myutils.get_segments_dataset(ds, mps[i], params["window_len_left"], params["window_len_right"])
        segs[i] = myutils.normalize_dataset(segs[i])
        if i<=1:
            mps[i+4], res2 = find_min_points_for_dataset(ds, True, params, print_summary)            
            segs[i+4] = myutils.get_segments_dataset(ds, mps[i+4], params["window_len_left"], params["window_len_right"])
            segs[i+4] = myutils.normalize_dataset(segs[i+4])
            print_summary_lab(res)
            #print_summary_lab(res2)
        else:            
            print_summary_free(res)        
        
    return mps, segs


# In[ ]:

#params = myparams.get_params()
#myparams.print_params(params)
#mps = find_min_points_all(params)
#params["x_th_max"] = -3
#mps = find_min_points_all(params)


# In[ ]:



