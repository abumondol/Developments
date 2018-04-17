function spmo_plot_on_sphere_ex(data, points, ico, ico_number, ttl)    
    t = ico(ico_number).triangles;
    e = ico(ico_number).edges;
    v = ico(ico_number).vertices;
    vp = ico(ico_number).voronoi_points;
    vr = ico(ico_number).voronoi_regions;

    figure;
    r = 0.99;
    [x,y,z] = sphere(20);
    mesh(x*r, y*r, z*r, 'FaceColor',[0.95 0.95 0.95], 'LineStyle','none'); 
    hold on;
    title(ttl);
    xlabel('X'); ylabel('Y'); zlabel('Z');    

    vr_count = length(vr);
    for i = 1:vr_count         
        a = vr{i};
        a = [a; a(1,:)];
        a = get_smaller(a);
        plot3(a(:,1), a(:, 2), a(:, 3));    
    end

    a = [1:length(v)]'; b = num2str(a); c = cellstr(b);
    dx = 1.05; % displacement so the text does not overlay the data points
    text(v(:,1)*dx, v(:,2)*dx, v(:,3)*dx, c);

    %scatter3(v(:,1), v(:, 2), v(:, 3), 'b.');
    
    %%%%%%%%%%%%%%%%%%%%%% ploting data %%%%%%%%%%%%%%%%%%%%%%%%
    
    seg_count = length(data)
    for i=1:seg_count
        a = data{i};
        if i==1
            plot3(a(:,1), a(:, 2), a(:, 3),'r', 'LineWidth', 1.5);    
        elseif i==2
            plot3(a(:,1), a(:, 2), a(:, 3),'b--', 'LineWidth', 1.5);         
        else
            plot3(a(:,1), a(:, 2), a(:, 3),'g:', 'LineWidth', 1.5);
        end
    end
    
    
    if ~isempty(points)
        a = points;        
        scatter3(a(:,1), a(:, 2), a(:, 3), 'rx');    
    end
    
    
end


