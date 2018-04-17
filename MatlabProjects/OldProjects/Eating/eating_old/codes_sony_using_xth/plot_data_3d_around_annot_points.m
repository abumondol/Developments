load('procdata');
offset = 3;

for sub = 2:2
    sess_count = length(procdata(sub).session);
    for sess = 1:sess_count
        d1 = procdata(sub).session(sess).accel;        
        d2 = procdata(sub).session(sess).grav;        
        %d1 = d1(1:4:end,:);
        %d2 = d2(1:4:end, :);
        %d2 = d1(2:end, 2:4) - d1(1:end-1, 2:4);
        %d2 = [d2(1,:);d2];
        %d2 = [d1(:,1), d2];
        
        annots = procdata(sub).session(sess).annotations;
        annots = annots(annots(:,2)<1000, :);
        
        d1(:,2:4) = smooth(d1(:,2:4), 0.8);
        d2(:,2:4) = smooth(d2(:,2:4), 0.8);        

        annot_count = size(annots, 1)
        for i = 1:annot_count
            s = annots(i,1);
            label = annots(i,2);
            
            close all;
            figure('units','normalized','outerposition',[0 0 1 1]);
            
            d = d1;
            d = d(d(:,1) >= s-offset & d(:,1) <= s+offset, 2:4);            
            subplot(1,2,1)       % add first plot in 2 x 1 grid
            
            ttl = strcat('sub:', num2str(sub), ', sess:', num2str(sess), ', annot no:', num2str(i), ', time:', time2str(s), ', label:', num2str(label) );            
                      
            plot3(d(:,1), d(:,2), d(:,3));
            xlabel('X');
            ylabel('Y');
            zlabel('Z');
            title(ttl);            
            grid on
            
            %scatter(offset, 0, 'rd'); % annotated mark point
            
            d = d2;
            d = d(d(:,1) >= s-offset & d(:,1) <= s+offset, 2:4);            
            subplot(1,2,2)       % add second plot in 2 x 1 grid            
            plot3(d(:,1), d(:,2), d(:,3));
            grid on
            xlabel('X');
            ylabel('Y');
            zlabel('Z');
            
            waitforbuttonpress            
        end
                
    end
end

