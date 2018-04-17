
# coding: utf-8

# In[11]:


import numpy as np
import pickle
import sys
import data_process_utils as dpu


# In[18]:


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

def process_meal_annots_steven(annots, dr, dl, subject_code, session):
    meal_count = len(annots)    
    if subject_code == 5 and session == 0:
        meal_count = 9
    elif subject_code == 6 and session == 0:
        meal_count = 2
    elif subject_code == 102 and session == 1:
        meal_count = 1
    elif subject_code == 104 and session == 0:
        meal_count = 7
    
    j = 0
    a = np.zeros((meal_count, 5))
    for i in range(meal_count):        
        s = annots[i].split(",")
        a[i, 0] = float(s[1].rstrip())
        a[i, 1] = float(s[2].rstrip())
        
        
        if a[i,0]>dr[-1,0]:
            print('xxxxxxxxxxxxxxxxxxxxxxxxxxxxx')            
            print('Right: Start time is greater than data end time', a[i,0], dr[-1,0])
            print('xxxxxxxxxxxxxxxxxxxxxxxxxxxxx')
            a[i, 2] = -1
            
        if a[i,0]>dl[-1,0]:
            print('xxxxxxxxxxxxxxxxxxxxxxxxxxxxx')            
            print('Left: Start time is greater than data end time', a[i,0], dl[-1,0])
            print('xxxxxxxxxxxxxxxxxxxxxxxxxxxxx')
            a[i, 3] = -1
            
            
        if a[i,1]>dr[-1,0]:            
            print('.................................')            
            print('Right: Adjusted Meal end time is greater than data end time: ', dr[-1, 0], a[i,1])
            print('..................................')
            a[i, 2] = dr[-1,0]
        
        if a[i,1]>dl[-1,0]:            
            print('.................................')            
            print('Left: Adjusted Meal end time is greater than data end time: ', dl[-1, 0], a[i,1])
            print('..................................')
            a[i, 3] = dr[-1,0]
            
            #subject_code == 107 and session == 0 or subject_code == 107 and session == 2:            
         
        a[i, 4] = get_meal_code(s[3].rstrip().strip())        
        if a[i, 4]==-1:
            print(" \n\n******************* Meal code problem *************** code is -1\n\n")
            print(s[3])
            sys.exit(0)
            
    return a


# In[23]:


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
        _ , _ , grav = dpu.quat2mat(quat)
        d = np.concatenate((time_accel_gyro, grav), axis=1)
        dr = dpu.resample(d, sampling_rate)
        
        
        d = np.genfromtxt(path_left, delimiter=',')            
        d[:, 0] = d[:, 0]/1e9        
        time_accel_gyro = d[:, :7]
        quat = d[:, -3:]
        _ , _ , grav = dpu.quat2mat(quat)
        d = np.concatenate((time_accel_gyro, grav), axis=1)
        dl = dpu.resample(d, sampling_rate)        

        with open(path_annot) as file:            
            annots = file.readlines()
        da = process_meal_annots_steven(annots, dr, dl, subject_codes[subject], sess)        
        #annots[:, 0] = dpu.time_to_index(data, annots[:, 0])
        #annots[:, 1] = dpu.time_to_index(data, annots[:, 1])
        print("Right duration:", dr[0,0], dr[-1,0])
        print("Left duration:", dl[0,0], dl[-1,0])
        print(da)
        
        drs.append(dr)
        dls.append(dl)
        das.append(da)        

    data_right.append(drs)
    data_left.append(dls)
    data_annots.append(das)


# In[4]:


with open('C:/ASM/DevData/eating_steven/data/free/steven_data_free_right.pkl', 'wb') as file:
    pickle.dump(data_right, file)
    
with open('C:/ASM/DevData/eating_steven/data/free/steven_data_free_left.pkl', 'wb') as file:
    pickle.dump(data_left, file)
    
with open('C:/ASM/DevData/eating_steven/data/free/steven_data_free_annots.pkl', 'wb') as file:
    pickle.dump(data_annots, file)

