load('procdata');
offset = 2.5;


for sub = 2:2
    sess_count = length(procdata(sub).session);
    for sess = 4:sess_count
        d1 = procdata(sub).session(sess).grav;                
        annots = procdata(sub).session(sess).annotations;
        %d1(:,2:4) = smooth(d1(:,2:4), 0.8);        

        annot_count = size(annots, 1)
        for i = 1:annot_count
            s = annots(i,1)
            label = annots(i,2);            
            d = d1;
            d = d(d(:,1) >= s-offset & d(:,1) <= s+offset, :);                                                
            t = d(:,1); % time
            t = [t(1:end-1, :),  t(2:end, :)]
            y = d(:,2:4); %x, y, z axes val            
            mark_index = find(t(:,1)<=s & t(:,2)>s)
            mp = y(mark_index, :)
            norm(mp)
            ttl = strcat('sub:', num2str(sub), ', sess:', num2str(sess), ', annot no:', num2str(i), ', time:', time2str(s), ', label:', num2str(label) );            
            
            close all;
            figure;            
            plot3(y(:,1), y(:,2), y(:,3));            
            title(ttl);
            xlim([-10,10]);
            ylim([-10,10]);
            zlim([-10,10]);
            hold on
            grid on            
            scatter3(mp(:,1), mp(:,2), mp(:,3), 'rx'); % annotated mark point           
            waitforbuttonpress            
            %return
        end
                
    end
end

