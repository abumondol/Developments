data = realdisp_quat;
sub_count = length(data);
ico_count = length(ico);
realdisp_cell_assignment = [];

for sub = 1:sub_count
    sess_count = length(data(sub).session);
    for sess = 1:sess_count
        t = data(sub).session(sess).t;        
        pos_count = length(data(sub).session(sess).position);
        for pos = 1:pos_count         
            Rz = data(sub).session(sess).position(pos).Rz;            
            for ico_number = 1:ico_count
                fprintf('Finding cell assignemt:: sub: %d, sess:%d, pos:%d, ico:%d\n',sub, sess, pos, ico_number);
                [cell_assignment, cell_seq] = cell_assignment_and_sequence(t, Rz, ico, ico_number);
                realdisp_cell_assignment(sub).session(sess).position(pos).ico(ico_number).cell_assignment = cell_assignment;
                %realdisp_cell_assignment(sub).session(sess).position(pos).ico(ico_number).cell_sequence = cell_seq;
            end            
        end
    end
end

save('realdisp_cell_assignment', 'realdisp_cell_assignment');
