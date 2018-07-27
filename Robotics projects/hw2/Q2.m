% Calculate a path from qStart to xGoal
% input: qStart -> 1x4 joint vector describing starting configuration of
%                   arm
%        xGoal -> 3x1 position describing desired position of end effector
%        sphereCenter -> 3x1 position of center of spherical obstacle
%        sphereRadius -> radius of obstacle
% output -> qMilestones -> 4xn vector of milestones. A straight-line interpolated
%                    path through these milestones should result in a
%                    collision-free path. You may output any number of
%                    milestones. The first milestone should be qStart. The
%                    last milestone should place the end effector at xGoal.
function qMilestones = Q2(rob,sphereCenter,sphereRadius,qStart,xGoal)
    T0 = transl([xGoal(1) xGoal(2) xGoal(3)]);
    %goal position orientation
    qGoal = rob.ikine(T0,qStart,[1 1 1 0 0 0]);
    
    % alpha represents the step size
    alpha = 2;
    % Tree initialized by start node
    tree = [qStart,0];
    noOfCol = size(qStart,2);
    noOfIterations = 10000;
    
    for i = 1:noOfIterations
        % find qrand with the 10% probability of qrand being qGoal
        if rand() < 0.1
            qRand = qGoal;
        else
            %generates random angles in -180 to 180 range
            qRand = rand(1,noOfCol) * 2*pi -pi;
        end
        % check if robot is in collision
        if robotCollision(rob,qRand,sphereCenter,sphereRadius)
            continue;
        end 
        
        % finding nearest node
        nearIndex= FetchNearestNode(tree,qRand,noOfCol);
        qNear = tree(nearIndex,1:noOfCol); 
        
        %calculating orientation for new node of the tree 
        qNew = qRand - qNear;
        if norm(qNew) > alpha
            qNew = alpha * qNew / norm(qNew);
        end
        qNew = qNear + qNew;  
        
        % check if the path between new node and it's nearest node 
        % is collision free
        if Q1(rob,qNear,qNew,sphereCenter,sphereRadius)
            continue;
        end
        
        % add new node to the tree
        tree = [tree; qNew nearIndex];
        
        % break when reached goal
        if qNew == qGoal
            break;
        end
    end
    qMilestones = getPath(tree);
    
    
end

function nearIndex = FetchNearestNode(tree,qRand,noOfCol)
  edist = zeros(size(tree,1),1);
  % calculating euclidean distance
  for i= 1: size(tree,1)
      edist(i) = euclideanDist(tree(i,1:noOfCol),qRand);  
  end
  % finding nearest node index
  [~,index] = min(edist);
  % fetching nearest node
   nearIndex = index;
end

function eDist = euclideanDist(q1,q2)
  % calculating euclidean distance between the given two vectors
  q = q1 -q2;
  eDist = sqrt(q * q');
end
 
function qPath = getPath(tree)
    m = 10;
    idx = size(tree,1);
    path = tree(end,1:end-1);
    
    while(idx ~= 1)
        
        curr = tree(idx,1:end-1);
        idx = tree(idx,end);
        next = tree(idx,1:end-1);
        path = [path;curr];
        
    end
    path = [path;next];
    qPath = path(end:-1:1,:);
    
end