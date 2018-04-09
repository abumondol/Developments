
# coding: utf-8

# In[5]:


import numpy as np
import pickle
import sys
import data_process_utils as dpu


# In[6]:


def get_meal_code(meal):    
    code = -1
    if meal == "meal" or meal=="lunch" or meal=="dinner":
        code = 1
    elif meal=="snack":
        code = 2    
    elif meal=="drink":
        code = 3
    return code;

def process_meal_annots_steven(annots, data, subject_code, session):
    meal_count = len(annots)
    sample_count = len(data)
    if subject_code == 5 and session == 0:
        meal_count = 9
    elif subject_code == 6 and session == 0:
        meal_count = 2
    elif subject_code == 102 and session == 1:
        meal_count = 1
    elif subject_code == 104 and session == 0:
        meal_count = 7
    
    j = 0
    a = np.zeros((meal_count, 3))
    for i in range(meal_count):        
        s = annots[i].split(",")
        t1 = float(s[1].rstrip())
        t2 = float(s[2].rstrip())
        
        if t1>data[-1,0]:
            print('xxxxxxxxxxxxxxxxxxxxxxxxxxxxx')            
            print('Error Meal start time is greater than data end time')
            print('xxxxxxxxxxxxxxxxxxxxxxxxxxxxx')
            sys.exit(0)
            
        if t2>data[-1,0]:            
            print('.................................')            
            print('Adjusted Meal end time is greater than data end time: ', data[-1, 0], t2)
            print('..................................')
            t2 = data[-1,0]
            
            #subject_code == 107 and session == 0 or subject_code == 107 and session == 2:
            
        a[i, 0] = t1
        a[i, 1] = t2
        a[i, 2] = get_meal_code(s[3].rstrip().strip())        
        if a[i, 2]==-1:
            print(" \n\n******************* Meal code problem *************** code is -1\n\n")
            
    return a


# In[7]:


path = 'C:/ASM/PublicData/eating_steventech/free'
sampling_rate = 16 #Hz
data_all = []

subject_codes = [2, 3, 4, 5, 6, 101, 102, 103, 104, 107, 109]
for subject in range(len(subject_codes)):
    dsubject = [];
    sess_count = 2
    if subject_codes[subject] == 107:
        sess_count = 5

    for sess in range(sess_count):
        print('\nSubject, code, Sess: ', subject, subject_codes[subject], sess)
        
        if subject_codes[subject] < 10:
            filePathAccel = path + "/0" + str(subject_codes[subject]) + "/000" + str(sess) + "/watch_right_000" + str(sess) + ".csv";
            filePathAnnots = path + "/0" + str(subject_codes[subject]) + "/000" + str(sess) + "/meal_events.csv";
        elif subject_codes[subject] == 109:
            filePathAccel = path + "/" + str(subject_codes[subject]) + "/000" + str(sess+3) + "/watch_right_000" + str(sess+3) + ".csv";
            filePathAnnots = path + "/" + str(subject_codes[subject]) + "/000" + str(sess+3) + "/meal_events.csv";
        else:
            filePathAccel = path + "/" + str(subject_codes[subject]) + "/000" + str(sess) + "/watch_right_000" + str(sess) + ".csv";
            filePathAnnots = path + "/" + str(subject_codes[subject]) + "/000" + str(sess) + "/meal_events.csv";

        data = np.genfromtxt(filePathAccel, delimiter=',')
        t = data[:, 0]/1e9
        t = t.reshape((-1, 1))
        accel_gyro = data[:, 1:7]
        quat = data[:, -3:]
        _ , _ , grav = dpu.quat2mat(quat)

        data = np.concatenate((t, grav, accel_gyro), axis=1)
        data = dpu.resample(data, sampling_rate)

        with open(filePathAnnots) as file:            
            annots = file.readlines()
        annots = process_meal_annots_steven(annots, data, subject_codes[subject], sess)        
        annots[:, 0] = dpu.time_to_index(data, annots[:, 0])
        annots[:, 1] = dpu.time_to_index(data, annots[:, 1])
        
        dsess = {}
        dsess['data'] = data
        dsess['annots'] = annots.astype(int)
        dsess['subject_code'] = subject_codes[subject]
        dsubject.append(dsess)        

    data_all.append(dsubject)


# In[8]:


with open('C:/ASM/DevData/eating/data/steven_free_data.pkl', 'wb') as file:
    pickle.dump(data_all, file)

