
# coding: utf-8

# In[ ]:


import numpy as np
import copy
import os
#from IPython.display import clear_output
import pickle

import find_potential_bites as fpb
import data_manager as dm
import myutils
import myparams
import mycnn


# In[ ]:


def get_train_data(data, exclude_subject):    
    X_train, Y_train = [], []
    for i in range(4):        
        for subject in range(len(data[i])):
            if (i ==1 and subject == exclude_subject+2) or (i ==3 and subject == exclude_subject):
                continue
            for sess in range(len(data[i][subject])):
                x = data[i][subject][sess]["x"]
                y = data[i][subject][sess]["y"]
                
                if i<2:
                    if len(X_train)==0:
                        X_train = x
                        Y_train = y
                    else:
                        X_train = np.concatenate((X_train, x), axis = 0)
                        Y_train = np.concatenate((Y_train, y), axis = 0)
                        
                elif i==3:
                    x = x[y==0, :, :]
                    y = y[y==0]
                    
                    X_train = np.concatenate((X_train, x), axis = 0)
                    Y_train = np.concatenate((Y_train, y), axis = 0)                                    
    
    return X_train, Y_train


# In[ ]:


def train_subject_wise(mps, seg_data, model_path, result_path):
    batch_size = 64
    epochs = 150
    print(model_path)
    print(result_path)
    
    res = []
    for subject in range(11):
        print("Subject: ", subject)
        X_train, Y_train = get_train_data(seg_data, subject)
        X_train = X_train.reshape(X_train.shape[0], X_train.shape[1], X_train.shape[2], 1)
        Y_train = Y_train.reshape(Y_train.shape[0], 1)
        print("X_train shape: ", X_train.shape)
        print("Y_train shape: ", Y_train.shape)
        model = mycnn.train_model(X_train, Y_train, batch_size, epochs)
        
        model.save(model_path+"model_"+str(subject)+".h5")
        print("Saved model to disk")

        print("Testing ...")
        res_lab = []
        if subject<=4:        
            path = result_path + "lab_"
            for sess in range(len(seg_data[5][subject+2])):
                X_test = seg_data[5][subject+2][sess]["x"]
                Y_test = seg_data[5][subject+2][sess]["y"]
                X_test = X_test.reshape(X_test.shape[0], X_test.shape[1], X_test.shape[2], 1)
                Y_test = Y_test.reshape(Y_test.shape[0], 1)            
                Y = mycnn.test_model(model, X_test, Y_test)                
                res_lab.append(Y)
                np.savetxt(path+str(subject)+"_"+str(sess)+".csv", Y, delimiter=",")

        res_free = []
        path = result_path + "free_"
        for sess in range(len(seg_data[3][subject])):
            X_test = seg_data[3][subject][sess]["x"]
            Y_test = seg_data[3][subject][sess]["y"]
            X_test = X_test.reshape(X_test.shape[0], X_test.shape[1], X_test.shape[2], 1)
            Y_test = Y_test.reshape(Y_test.shape[0], 1)        
            Y = mycnn.test_model(model, X_test, Y_test)            
            res_free.append(Y)
            np.savetxt(path+str(subject)+"_"+str(sess)+".csv", Y, delimiter=",")

        res_subject = {"lab":res_lab, "free":res_free}    
        res.append(res_subject)
        print(" Done")
        
    path = result_path + "res_all.pkl"    
    with open(path, "wb") as file:
        pickle.dump(res, file)


# In[ ]:


for window_len in range(48, 50, 8):
    params = myparams.get_params()
    params["window_len_left"] = window_len
    params["window_len_right"] = window_len 
    folder_path = myutils.get_file_name_from_params(params)        
    model_path = folder_path+dm.get_slash()+"models"+dm.get_slash()
    result_path = folder_path + dm.get_slash() + "results" + dm.get_slash()
    model_path = dm.create_folder(model_path)
    result_path = dm.create_folder(result_path)
    
    
    print("Model path: ", model_path)
    print("Result path: ", result_path)
    print("Window len: ", window_len)
    print("Finding potential bites and extracting segments ...")
    mps, segs = fpb.find_min_points_segments_all(params)
    dm.save_data(mps, "mps", folder_path)
    #dm.save_data(segs, "segments", folder_path)
    #clear_output()        
    print("Training ...")
    train_subject_wise(mps, segs, model_path, result_path)

