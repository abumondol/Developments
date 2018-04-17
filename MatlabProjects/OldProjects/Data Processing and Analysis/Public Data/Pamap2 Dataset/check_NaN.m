sub_count = length(pamap2_data)
for sub = 1:sub_count
    sess_count = length(pamap2_data(sub).session);
    for sess = 1:sess_count
        pos_count = length(pamap2_data(sub).session(sess).position);
        for pos = 1:pos_count
            fprintf('sub:%d, sess:%d, pos:%d\n', sub, sess, pos);
            a = pamap2_data(sub).session(sess).position(pos).accel;
            g = pamap2_data(sub).session(sess).position(pos).gyro;
            m = pamap2_data(sub).session(sess).position(pos).mag;
           
            count = length(Accelerometer);
            
            for i = 1:count
                if sum(isnan(a(i, :))) > 0 
                    i
                    waitforbuttonpress
                end
            end
            
            
            if sum(sum(isnan(a))) > 0 
                fprintf('=================== Nan in accle ========================\n');
            end
            
            if sum(sum(isnan(g))) > 0 
                fprintf('=================== Nan in gyro ========================\n');
            end
            
            if sum(sum(isnan(m))) > 0 
                fprintf('=================== Nan in mag ========================\n');
            end
            
        end
    end
end