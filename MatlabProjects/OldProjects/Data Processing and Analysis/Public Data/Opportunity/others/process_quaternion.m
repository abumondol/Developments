data = [];
sub_count = length(opp_data);
for sub = 1:sub_count
    sess_count = length(opp_data(sub).session);
    for sess = 1:sess_count        
        pos_count = length(opp_data(sub).session(sess).position);
        for pos = 1:pos_count
            fprintf('Processing subject %d, session %d, pos %d\n', sub, sess, pos);
            d = opp_data(sub).session(sess).position(pos).data;
            [Rx, Ry, Rz] = quaternion_to_rotation_matrix(d(:, 10:13));        
            %data(sub).session(sess).position(pos).Rx = Rx; 
            %data(sub).session(sess).position(pos).Ry = Ry;
            data(sub).session(sess).position(pos).grav = Rz; 
        end                    
    end    
end

save('data', 'data');