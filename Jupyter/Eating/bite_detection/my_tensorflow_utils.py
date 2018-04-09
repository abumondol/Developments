
# coding: utf-8

# In[3]:


import tensorflow as tf
import my_classification_utils as mcu
import numpy as np
import my_bite_detection_utils as bdu


# In[4]:


def conv_layer(x, size_in, size_out, ksize, strides, padding, name):    
    strides = [1, strides[0], strides[1], 1]   
    with tf.name_scope(name):
        W = tf.Variable(tf.truncated_normal([ksize[0], ksize[1], size_in, size_out], stddev=0.1), name="W")
        b = tf.Variable(tf.constant(0.1, shape=[size_out]), name="b")
        conv = tf.nn.conv2d(x, W, strides=strides, padding=padding)
        output = tf.nn.relu(conv + b)
        tf.summary.histogram("weights", W)
        tf.summary.histogram("biases", b)
        tf.summary.histogram("outputs", output)
        return output
    
def maxpool_layer(x, ksize, strides, padding, name):
    ksize = [1, ksize[0], ksize[1], 1]
    strides = [1, strides[0], strides[1], 1]
    with tf.name_scope(name):
        output = tf.nn.max_pool(x, ksize=ksize, strides=strides, padding=padding)
        tf.summary.histogram("outputs", output)
        return output
    
def fc_layer(x, size_in, size_out, name, relu=False):
    with tf.name_scope(name):
        W = tf.Variable(tf.truncated_normal([size_in, size_out], stddev=0.1), name="W")
        b = tf.Variable(tf.constant(0.1, shape=[size_out]), name="b")
        output = tf.matmul(x, W) + b
        if relu:
            output = tf.nn.relu(output)
            
        tf.summary.histogram('weights', W)
        tf.summary.histogram('biases', b)
        tf.summary.histogram('outputs', output)
        return output
    


# In[5]:


def one_axis_conv_net(x, name):
    x_shape =x.get_shape().as_list()
    print('Inside one_axis_net: ',name,', x_shape', x_shape)
    
    axis_count = x_shape[-1]
    print("  Axis count: ", axis_count)
    with tf.name_scope(name):
        x = tf.reshape(x, shape=[-1, x.shape[1], x.shape[2], 1], name="reshape")
        conv_1 = conv_layer(x, size_in=1, size_out=16, ksize=[5,axis_count], strides=[1,axis_count], padding="VALID", name='conv_1')        
        maxpool_1 = maxpool_layer(conv_1, ksize=[2,1], strides=[2,1], padding="VALID", name="maxpool_1")
        print("  Conv_1, maxpool_1 shape: ", conv_1.get_shape().as_list(), maxpool_1.get_shape().as_list())

        conv_2 = conv_layer(maxpool_1, size_in=16, size_out=32, ksize=[5,1], strides=[1,1], padding="VALID", name='conv_2')
        maxpool_2 = maxpool_layer(conv_2, ksize=[2,1], strides=[2,1], padding="VALID", name="maxpool_2")
        print("  Conv_2, maxpool_2 shape: ", conv_2.get_shape().as_list(), maxpool_2.get_shape().as_list())
        
        conv_3 = conv_layer(maxpool_2, size_in=32, size_out=64, ksize=[5,1], strides=[1,1], padding="VALID", name='conv_3')
        maxpool_3 = maxpool_layer(conv_3, ksize=[2,1], strides=[2,1], padding="VALID", name="maxpool_3")
        print("  Conv_3, maxpool_3 shape: ", conv_3.get_shape().as_list(), maxpool_3.get_shape().as_list())
        
        sz = maxpool_3.get_shape().as_list()
        flattened = tf.reshape(maxpool_3, shape=[-1, sz[1]*sz[2]*sz[3]], name="Flattened")
        return flattened


# In[9]:


def all_axes_net(x, y, keep_prob, name):
    x_shape = x.get_shape().as_list()
    y_shape = y.get_shape().as_list()
    print('Inside all_axes_net: x_shape, y_shape :', x_shape, y_shape)
    print(type(x_shape))
    
    with tf.name_scope(name):
        all_axes = list(range(x_shape[-1]))
        for i in range(x_shape[-1]):
            axis_data = x[:, :, i]
            axis_data = tf.reshape(axis_data, shape=[-1, x.shape[1], 1], name="reshape")
            #axis_data = tf.slice(x, [0,0,i], [-1, -1, 1], name="slice_"+str(i))
            all_axes[i] = one_axis_conv_net(axis_data, name="conv_axis_"+str(i))
            print("Shape ", i, all_axes[i].get_shape().as_list())
            
        
        for i in range(0, x_shape[-1], 3):
            axis_data = x[:, :, i:i+3]            
            a = one_axis_conv_net(axis_data, name="conv_3_axes_"+str(i))
            all_axes.append(a)
            print("Shape ", i, all_axes[-1].get_shape().as_list())
        
        print("All axis list size:", len(all_axes))        
        combo_flattened = tf.concat(all_axes, axis=1, name='concat_axis')
        combo_shape = combo_flattened.get_shape().as_list()
        print("Combo shape one net: ", combo_shape)

        fc_1 = fc_layer(combo_flattened, combo_shape[-1], 512, name='FC_1', relu=True)
        fcdrop_1 = tf.nn.dropout(fc_1, keep_prob, name='fcdrop_1')    
        fc_2 = fc_layer(fcdrop_1, 512, 512, name='FC_2', relu=True)
        fcdrop_2 = tf.nn.dropout(fc_2, keep_prob, name='fcdrop_2') 
        #fc_3 = fc_layer(fcdrop_2, 512, 512, name='FC_3', relu=True)
        #fcdrop_3 = tf.nn.dropout(fc_3, keep_prob, name='fcdrop_3') 
        logits = fc_layer(fcdrop_2, 512, y_shape[-1], name='FC_Final')
        return logits


# In[7]:


def combo_net(xleft, xmid, xright, y, keep_prob):
    xmid_shape = xmid.get_shape().as_list()
    xleft_shape = xleft.get_shape().as_list()
    xright_shape = xright.get_shape().as_list()
    y_shape = y.get_shape().as_list()
    print('Inside combo net(Shapes): xmid, xmid, xright, y :', xmid_shape, xleft_shape, xright_shape, y_shape)
    
    fc_left = all_axes_net(xleft, y, keep_prob, name="left")
    fc_mid = all_axes_net(xmid, y, keep_prob, name="left")
    fc_right = all_axes_net(xright, y, keep_prob, name="left")
    
    combo_flattened = tf.concat([fc_left, fc_mid, fc_right], axis=1, name='concat_combo')
    combo_shape = combo_flattened.get_shape().as_list()
    print("Combo shape all net: ", combo_shape)
    
    fc_1 = fc_layer(combo_flattened, combo_shape[-1], 512, name='FC_1_combo', relu=True)
    fcdrop_1 = tf.nn.dropout(fc_1, keep_prob, name='fcdrop_1_combo')    
    #fc_2 = fc_layer(fcdrop_1, 512, 256, name='FC_2_combo', relu=True)
    #fcdrop_2 = tf.nn.dropout(fc_2, keep_prob, name='fcdrop_2_combo')
    logits = fc_layer(fcdrop_1, 512, y_shape[-1], name='FC_Final_combo')
    
    return logits    
    


# In[8]:


def adjust_for_batch_size(train_x, train_y, batch_size):
    train_count = len(train_x)    
    last_batch_size = train_count%batch_size
    if last_batch_size >0:
        last_batch_gap = batch_size - last_batch_size        
        train_x = np.concatenate((train_x, train_x[0:last_batch_gap]), axis=0)
        train_y = np.concatenate((train_y, train_y[0:last_batch_gap]), axis=0)
        
    train_count = len(train_x)    
    assert train_count%batch_size == 0
    return train_x, train_y


# In[2]:


def train_test_model(train_x, train_y, test_x, test_y, folder_path, params, save_model=True):
    learning_rate = params['learning_rate']
    num_epochs = params['num_epochs']
    batch_size = params['batch_size']
    keep_prob_val = params['keep_prob_val']
    
    train_result, test_result = [], []
    tf.reset_default_graph()
    
    x = tf.placeholder(tf.float32, [None, train_x.shape[1], train_x.shape[2]], name="x")
    y = tf.placeholder(tf.float32, [None, train_y.shape[1]], name="y")
    keep_prob = tf.placeholder(tf.float32, name="keep_prob")    
    
    #a = train_x.shape[1]//3
    #b = 2*a
    #xleft = x[:, 0:a, :]
    #xmid = x[:, a:b, :]
    #xright = x[:, b:, :]    
    #print("Window shapes: ", xleft.shape, xmid.shape, xright.shape)    
    #logits = mtu.combo_net(xleft, xmid, xright, y, keep_prob)
    
    logits = all_axes_net(x, y, keep_prob, name="all_axes_net")
    print("Logit shape: ",logits.get_shape().as_list())
    prediction = tf.nn.sigmoid(logits, name="prediction")
    correct_prediction = tf.equal(tf.argmax(prediction, 1), tf.argmax(y, 1), name="correct_prediction")
    

    loss_op = tf.reduce_mean(
        tf.nn.sigmoid_cross_entropy_with_logits(logits=logits, labels=y), name="loss_op")
    #tf.summary.scalar("loss_op_summary", loss_op)

    train_step = tf.train.AdamOptimizer(learning_rate).minimize(loss_op, name="train_step")

    
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32), name="accuracy")    
    #tf.summary.scalar("accuracy_summary", accuracy)

    #summ = tf.summary.merge_all()    
    
    sess = tf.Session()
    sess.run(tf.global_variables_initializer())
    #LOGDIR = folder_path +"/tensorboard"
    #bdu.create_directory(LOGDIR)
    #writer = tf.summary.FileWriter(LOGDIR)
    #writer.add_graph(sess.graph)

    train_x, train_y = adjust_for_batch_size(train_x, train_y, batch_size)   
    
    train_count = len(train_x)
    for epoch in range(num_epochs):
        print("Epoch:", epoch)
        for ix in range(0, train_count, batch_size):                            
            batch_x, batch_y = train_x[ix:ix+batch_size], train_y[ix:ix+batch_size]
            sess.run(train_step, feed_dict={x:batch_x, y:batch_y, keep_prob:keep_prob_val})            
            
        #loss, acc, s = sess.run([loss_op, accuracy, summ], feed_dict={x: train_x, y: train_y, keep_prob:1.0})                
        #writer.add_summary(s, epoch)
        
        loss, acc = sess.run([loss_op, accuracy], feed_dict={x: train_x, y: train_y, keep_prob:1.0})        
        pred = sess.run(prediction, feed_dict={x:train_x, y: train_y, keep_prob:1.0})        
        tn, tp, fn, fp, pr, rc, sp, f1 = mcu.get_metrics(pred, train_y)        
        train_result.append([epoch, loss, acc, tn, tp, fn, fp, pr, rc, sp, f1])
        print('  Train loss: {:.4f}, acc: {:.4f}, pr: {:.4f}, rc: {:.4f}, sp: {:.4f}, f1: {:.4f}'.format(loss, acc, pr, rc, sp, f1))
        print('                                   tn: {:.0f}, tp: {:.0f}, fn: {:.0f}, fp: {:.0f}'.format(tn, tp, fn, fp))
        
        
        loss, acc = sess.run([loss_op, accuracy], feed_dict={x: test_x, y: test_y, keep_prob:1.0})                
        pred = sess.run(prediction, feed_dict={x:test_x, y: test_y, keep_prob:1.0})        
        tn, tp, fn, fp, pr, rc, sp, f1 = mcu.get_metrics(pred, test_y)        
        test_result.append([epoch, loss, acc, tn, tp, fn, fp, pr, rc, sp, f1])
        print('  Test  loss: {:.4f}, acc: {:.4f}, pr: {:.4f}, rc: {:.4f}, sp: {:.4f}, f1: {:.4f}'.format(loss, acc, pr, rc, sp, f1))        
        print('                                   tn: {:.0f}, tp: {:.0f}, fn: {:.0f}, fp: {:.0f}'.format(tn, tp, fn, fp))
        
    
    print('Optimization Finished!')
    
    if save_model:
        saver = tf.train.Saver()    
        path = folder_path +"/model"
        bdu.create_directory(path)
        saver.save(sess, path+'/model')    
    
    test_pred = sess.run(prediction, feed_dict={x:test_x, y: test_y, keep_prob:1.0})    
    sess.close()
        
    return test_pred, train_result, test_result


# In[ ]:
































