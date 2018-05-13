
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfu
import my_data_process_utils as mdpu


# In[3]:


def get_meal_code(meal):    
    code = -1
    if meal == "meal" or meal=="lunch" or meal=="dinner":
        code = 1
    elif meal=="snack":
        code = 2    
    elif meal=="drink":
        code = 3
    elif meal=="eat":
        code = 4
    return code

def process_meal_annots(annots, subject_code, session, sampling_rate):
    meal_count = len(annots)
    '''
    if subject_code == 5 and session == 0:
        meal_count = 9
    elif subject_code == 6 and session == 0:
        meal_count = 2
    elif subject_code == 102 and session == 1:
        meal_count = 1
    elif subject_code == 104 and session == 0:
        meal_count = 7
    '''
    
    j = 0
    a = np.zeros((meal_count, 3))
    for i in range(meal_count):        
        s = annots[i].split(",")
        a[i, 0] = float(s[1].rstrip())*sampling_rate
        a[i, 1] = float(s[2].rstrip())*sampling_rate       
        a[i, 2] = get_meal_code(s[3].rstrip().strip())        
        assert a[i, 2]!=-1
        a = a.astype(int)
        
    return a


# In[4]:


path = 'C:/ASM/PublicData/eating_steventech/free'
sampling_rate = 16 #Hz
data_right = []
data_left = []
data_annots = []

subject_codes = [2, 3, 4, 5, 6, 101, 102, 103, 104, 107, 109]
for subject in range(len(subject_codes)):
    drs, dls, das = [], [], []
    sess_count = 2
    if subject_codes[subject] == 107:
        sess_count = 5

    for sess in range(sess_count):
        print('\nSubject, Sess, Code: ', subject, sess, subject_codes[subject])
        
        if subject_codes[subject] < 10:
            path_right = path + "/0" + str(subject_codes[subject]) + "/000" + str(sess) + "/watch_right_000" + str(sess) + ".csv"
            path_left = path + "/0" + str(subject_codes[subject]) + "/000" + str(sess) + "/watch_left_000" + str(sess) + ".csv"
            path_annot = path + "/0" + str(subject_codes[subject]) + "/000" + str(sess) + "/meal_events.csv"
        elif subject_codes[subject] == 109:
            path_right = path + "/" + str(subject_codes[subject]) + "/000" + str(sess+3) + "/watch_right_000" + str(sess+3) + ".csv"
            path_left = path + "/" + str(subject_codes[subject]) + "/000" + str(sess+3) + "/watch_left_000" + str(sess+3) + ".csv"
            path_annot = path + "/" + str(subject_codes[subject]) + "/000" + str(sess+3) + "/meal_events.csv"
        else:
            path_right = path + "/" + str(subject_codes[subject]) + "/000" + str(sess) + "/watch_right_000" + str(sess) + ".csv"
            path_left = path + "/" + str(subject_codes[subject]) + "/000" + str(sess) + "/watch_left_000" + str(sess) + ".csv"
            path_annot = path + "/" + str(subject_codes[subject]) + "/000" + str(sess) + "/meal_events.csv"

        d = np.genfromtxt(path_right, delimiter=',')
        d[:, 0] = d[:, 0]/1e9        
        time_accel_gyro = d[:, :7]
        quat = d[:, -3:]
        _ , _ , grav = mdpu.quat2mat(quat)
        linaccel = time_accel_gyro[:, 1:4] - (grav*9.81)
        
        d = np.concatenate((time_accel_gyro, linaccel, grav), axis=1)
        dr = mdpu.resample(d, sampling_rate)
        
        
        d = np.genfromtxt(path_left, delimiter=',')            
        d[:, 0] = d[:, 0]/1e9        
        time_accel_gyro = d[:, :7]
        quat = d[:, -3:]
        _ , _ , grav = mdpu.quat2mat(quat)
        linaccel = time_accel_gyro[:, 1:4] - (grav*9.81)
        
        d = np.concatenate((time_accel_gyro, linaccel, grav), axis=1)
        dl = mdpu.resample(d, sampling_rate)        

        with open(path_annot) as file:            
            annots = file.readlines()
        da = process_meal_annots(annots,subject_codes[subject], sess, sampling_rate)        
        
        print("Right duration:", dr[0,0], dr[-1,0])
        print("Left duration:", dl[0,0], dl[-1,0])
        print(da)
        
        drs.append(dr)
        dls.append(dl)
        das.append(da)        

    data_right.append(drs)
    data_left.append(dls)
    data_annots.append(das)


# In[7]:


mfu.write_file('data', 'free_data_steven_right.pkl', data_right)
mfu.write_file('data', 'free_data_steven_left.pkl', data_left)
mfu.write_file('data', 'free_data_steven_annots.pkl', data_annots)
    


# In[8]:


print(data_right[0][0].shape)

