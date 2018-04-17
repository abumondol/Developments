load('rawdata');
procdata = [];
subject_count = length(rawdata);
for sub=1:subject_count
    session_count =  length(rawdata(sub).session);
    for sess=1:session_count
        if sub==2 && sess ==5
            continue
        end
        
        fprintf('Subject: %d, Session:%d\n', sub, sess);
        d = rawdata(sub).session(sess).data;
        annots = rawdata(sub).session(sess).annotations;
        offset = rawdata(sub).session(sess).offset;

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

        accel_annot_indices = get_annot_indices(accel, annots);
        gyro_annot_indices = get_annot_indices(gyro, annots);
        grav_annot_indices = get_annot_indices(grav, annots);
        quat_annot_indices = get_annot_indices(quat, annots);

        %accel(:,2:4) =  smooth(accel(:,2:4), 0.8);
        %grav(:,2:4) =  smooth(grav(:,2:4), 0.8);
        
        procdata(sub).session(sess).accel = accel;            
        procdata(sub).session(sess).gyro = gyro;            
        procdata(sub).session(sess).grav = grav;
        procdata(sub).session(sess).linaccel = linaccel;
        procdata(sub).session(sess).quat = quat;
        
        
        procdata(sub).session(sess).accel_annot_indices = accel_annot_indices;
        procdata(sub).session(sess).gyro_annot_indices = gyro_annot_indices;
        procdata(sub).session(sess).grav_annot_indices = grav_annot_indices;
        procdata(sub).session(sess).quat_annot_indices = quat_annot_indices;
        
        
        
        procdata(sub).session(sess).annotations = sortrows(rawdata(sub).session(sess).annotations);
        procdata(sub).session(sess).annot_count = size(annots, 1);
        procdata(sub).session(sess).pos_annot_count = sum(annots(:,2)<1000);
    end
end

save('procdata', 'procdata');
