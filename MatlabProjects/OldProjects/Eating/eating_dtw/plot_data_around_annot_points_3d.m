%load('data')
offset = 4;

for sub = 10:10    
    d1 = data(sub).accel;
    rate = length(d1)/(d1(end,1)-d1(1,1))
        
    annots = data(sub).annots;
    annots2 = data(sub).annots_adjusted;
    
    annot_points = get_annot_indices(d1, annots);
    annot_points2 = get_annot_indices(d1, annots2);

    annot_count = size(annots, 1);
    for i = 1:annot_count
        s = annots(i,1);        
        label = annots(i,2);            
        %if label>=400; continue; end
        close all;
        figure;%('units','normalized','outerposition',[0 0 1 1]);
        
        d = d1;
        d = d(d(:,1) >= s-offset & d(:,1) <= s+offset, 2:4);             
        ttl = strcat('sub:', num2str(sub), ', annot no:', num2str(i), ', time:', num2str(annots2(i,1)), ', label:', num2str(label) );            
        
        plot3(d(:,1), d(:,2), d(:,3));        
        xlim([-10,10]); ylim([-10,10]); zlim([-10,10]);        
        xlabel('X'); ylabel('Y'); zlabel('Z');
        title(ttl);
        hold on
        grid on     
        
        p = d1(annot_points(i,1), 2:4)
        scatter3(p(1), p(2), p(3), 'rd', 'filled'); % annotated mark point
        p = d1(annot_points2(i,1), 2:4)
        scatter3(p(1), p(2), p(3), 'go', 'filled'); % annotated mark point adjusted        

        waitforbuttonpress        
    end
                
end


