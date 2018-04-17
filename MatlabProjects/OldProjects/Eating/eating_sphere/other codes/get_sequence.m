function seq = get_sequence(accel, neighbor_distance_angle)    
    accel_count = size(accel, 1);
    seq = zeros(accel_count, 5);            
    
    min_proximity = cos(neighbor_distance_angle*pi/180);
    
    seq_no = 1;
    center = accel(1, :);        
    start_index = 1;
    for i = 2:accel_count
        proximity = sum(center.* accel(i, :), 2);  %cos_theta
        if proximity>= min_proximity            
            continue
        end
        
        seq(seq_no, :) = [center, start_index, i-1];
        seq_no = seq_no + 1;
        start_index = i;
        center = accel(i, :);
    end
    
    seq(seq_no, :) = [center, start_index, accel_count];
    seq = seq(1:seq_no, :);
    seq = [seq, seq(:,5)-seq(:,4)+1];

end