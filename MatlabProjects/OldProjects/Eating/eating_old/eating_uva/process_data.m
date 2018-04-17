load('raw_data');
data = [];
subject_count = length(raw_data);
for sub=1:subject_count
    session_count =  length(raw_data(sub).session);
    for sess=1:session_count
        fprintf('Subject: %d, Session:%d\n', sub, sess);
        d = raw_data(sub).session(sess).data;
        annots = raw_data(sub).session(sess).annotations;
        offset = raw_data(sub).session(sess).offset;

        if d(1,2) == 0
            d = d(2:end, :);
        end

        d(:,1) = d(:,1)-d(1,1);
        d(:,1) = d(:,1)/1e9 + offset;

        if sub==2 && sess==1 %segmented video, discard data from second segment
            d = d(d(:,1) <= annots(end,1)+ 5, :); % put 5 second more beyond last annotation of the first video
        end

        accel = d(d(:,2)==1, [1, 4:6]);
        gyro = d(d(:,2)==4, [1, 4:6]);
        grav = d(d(:,2)==9, [1, 4:6]);
        quat = d(d(:,2)==11, [1, 4:6]);
        
        s = find(accel(:,1) == grav(1,1));
        linaccel = [grav(:,1), accel(s:end, 2:4) - grav(:, 2:4)]; 

        %accel = accel(1:4:end, :);
        accel(:, 2:4) = smooth(accel(:,2:4), 0.90); 
        
        accel_annot_indices = get_annot_indices(accel, annots);
        gyro_annot_indices = get_annot_indices(gyro, annots);
        grav_annot_indices = get_annot_indices(grav, annots);
        quat_annot_indices = get_annot_indices(quat, annots);

        data(sub).session(sess).accel = accel;            
        data(sub).session(sess).gyro = gyro;            
        data(sub).session(sess).grav = grav;
        data(sub).session(sess).linaccel = linaccel;
        data(sub).session(sess).quat = quat;
        data(sub).session(sess).accel_annot_indices = accel_annot_indices;
        data(sub).session(sess).gyro_annot_indices = gyro_annot_indices;
        data(sub).session(sess).grav_annot_indices = grav_annot_indices;
        data(sub).session(sess).quat_annot_indices = quat_annot_indices;
        
        data(sub).session(sess).annotations = sortrows(raw_data(sub).session(sess).annotations);
        data(sub).session(sess).annot_count = size(annots, 1);
        if length(annots) > 0
            data(sub).session(sess).pos_annot_count = sum(annots(:,2)<1000);
        end
    end
end

save('data', 'data');
