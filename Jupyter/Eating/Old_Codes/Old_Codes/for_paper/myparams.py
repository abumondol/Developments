
# coding: utf-8

# In[22]:

def get_params():
    params = {}
    
    params["smooth_factor"] = "800"
    
    params["x_th_max"] = -3
    params["min_bite_interval"] = 40
    params["var_th"] = 1
    
    params["window_len_left"] = 48
    params["window_len_right"] = 48
    params["max_annot_distance"] = 32
    params["exclude_annot_distance"] = 64
    
    return params
    
def print_params(params):
    print("----------------------------------")
    print("List of paramters and their values")
    print("----------------------------------")
    for key, value in params.items():
        print(key+" : "+str(value))
    print("") 

