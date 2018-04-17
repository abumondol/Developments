data = realdisp_data;
activity_chunks = realdisp_activity_chunks;
sub_count = length(data);

for sub = 1:sub_count
    sess_count = length(data(sub).session);
    for sess = 1:sess_count
        t = data(sub).session(sess).t;
        chunks = activity_chunks(sub).session(sess).chunks;                    
        chunk_count = size(chunks, 1);
        
        pos_count = length(data(sub).session(sess).position);
        for pos = 1:pos_count
            cell_sequence = [];
            for i = 1:chunk_count
                c1 = chunks(i,2);
                c
                [cell_assignment, cell_seq] = cell_assignment_and_sequence(t, data, ico, ico_number);
                cell_sequence(i).cell_assignment = cell_assignment;
                cell_sequence(i).cell_sequence = cell_seq;
                cell_sequence(i).summary = cell_sequence_summary(cell_seq);
            end            
        end

        activity_chunks(sub).session(sess).position(pos).cell_sequence = cell_sequence;                                    
        
    end
end

realdisp_activity_chunks = activity_chunks;
save('realdisp_activity_chunks', 'realdisp_activity_chunks');
