sub_count = length(mhealth_data)
for sub = 1:sub_count
    sess_count = length(mhealth_data(sub).session);
    for sess = 1:sess_count
        pos_count = length(mhealth_data(sub).session(sess).position);
        for pos = 1:pos_count
            fprintf('sub:%d, sess:%d, pos:%d\n', sub, sess, pos);
            a = mhealth_data(sub).session(sess).position(pos).accel;
            g = mhealth_data(sub).session(sess).position(pos).gyro;
            %m = mhealth_data(sub).session(sess).position(pos).mag;
           
            count = length(a);
            
            if sum(sum(isnan(a))) > 0 
                fprintf('=================== Nan in accle ========================\n');
            end
            
            if sum(sum(isnan(g))) > 0 
                fprintf('=================== Nan in gyro ========================\n');
            end
                        
        end
    end
end