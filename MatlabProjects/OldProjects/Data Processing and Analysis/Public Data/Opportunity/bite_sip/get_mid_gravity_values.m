function mid_gs = get_mid_gravity_values(data, indices)
    mid_gs=[];
    for subj = 1:4
        for sess = 1:6            
            g = data{subj, sess}.grav;
            ix = indices(indices(:,1)==subj & indices(:,2)==sess, 5);
            ix_count = length(ix);
            mid_gs = [mid_gs; zeros(ix_count, 1)+subj, zeros(ix_count, 1)+sess, g(ix, :)];
        end
    end
end    
